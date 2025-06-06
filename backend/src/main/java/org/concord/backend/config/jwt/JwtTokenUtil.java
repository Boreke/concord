package org.concord.backend.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.exceptions.BusinessException;
import org.concord.backend.exceptions.http.HttpBadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class JwtTokenUtil {

  @Autowired
  UserRepository userRepository;

  final private static Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);
  final private static String SECRET_HASHING_ALGORITHM = "SHA-256";
  final private Locale locale = Locale.FRANCE;

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.refresh-secret}")
  private String refreshSecret;
  @Value("${jwt.issuer}")
  private String issuer;
  @Value("${jwt.token-duration}")
  private int tokenDuration;
  @Value("${jwt.refresh-token-duration}")
  private int refreshTokenDuration;
  
  Algorithm standardAlgorithm, refreshAlgorithm;
  
  @PostConstruct
  private void init() {
    standardAlgorithm = Algorithm.HMAC256(secret);
    refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
  }

  public DecodedJWT decodeToken(String token){

    Algorithm algorithm = Algorithm.HMAC256(secret);
    JWTVerifier verifier = JWT.require(algorithm)
      .withIssuer(issuer)
      .build();

    return verifier.verify(token);


  }

  public TokenSet generateTokenSet(User user) {
    TokenPayload tokenPayload = generatePayload(user);
    String token = generateToken(tokenPayload);
    String refreshToken = generateRefreshToken(tokenPayload);
    return new TokenSet(token, refreshToken);
  }

  public TokenPayload getPayload(DecodedJWT jwt) {
    Long id = Long.parseLong(jwt.getClaim("id").asString());
    String role = jwt.getClaim("role").asString();

    var payload = new TokenPayload();
    payload.setId(id);
    payload.setRole(role);

    return payload;
  }


  /**
   * Refresh the set of tokens (token + refresh token)
   * throws if the refresh token is not valid -> login needed
   *
   * @param token the refresh token
   * @return a set of new tokens
   * @throws JWTVerificationException
   */
  public TokenSet refreshWithToken(String token){
    try {
      DecodedJWT decodedJWT = JWT.decode(token);
      var payload = getPayload(decodedJWT);

      Long id = payload.getId();
      var user = userRepository
              .findById(id).orElse(null);

      if(user==null){
        throw new BusinessException("Unable to find user with this id");
      }

      JWTVerifier verifier = JWT.require(refreshAlgorithm)
        .withIssuer(issuer)
        .build();
      verifier.verify(token);
      return generateTokenSet(user);
    } catch (JWTVerificationException | HttpBadRequestException e) {

      LOG.error("could not refresh token (" +e.getMessage()+")");
    }
    throw new BusinessException("Failed to refresh token");
  }


  public TokenPayload generatePayload(User user) {
    TokenPayload tokenPayload = new TokenPayload();
    tokenPayload.setRole(user.getRole().toString());
    tokenPayload.setId(user.getId());
    return tokenPayload;
  }

  private String generateToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, tokenDuration);
    Map<String,String> payload = new HashMap<>();
    payload.put("id",tokenPayload.getId().toString());
    payload.put("role",tokenPayload.getRole());

    return JWT.create()
      .withIssuer(issuer)
      .withIssuedAt(Calendar.getInstance(locale).getTime())
      .withExpiresAt(calendar.getTime())
      .withPayload(payload)
      .sign(standardAlgorithm);

  }

  private String generateRefreshToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, refreshTokenDuration);
    Map<String,String> payload = new HashMap<>();
    payload.put("id",tokenPayload.getId().toString());
    return JWT.create()
      .withIssuer(issuer)
      .withIssuedAt(Calendar.getInstance(locale).getTime())
      .withExpiresAt(calendar.getTime())
      .withPayload(payload)
      .sign(refreshAlgorithm);


  }
}
