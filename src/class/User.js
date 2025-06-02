export class User {
    
    constructor(id, userTag, email, displayName, role, createdAt, isPrivate = false) {
        this.id = id;
        this.userTag = userTag;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
        this.isPrivate = isPrivate;
    }

    getId() {
        return this.id;
    }

    getEmail() {
        return this.email;
    }

    setEmail(email) {
        this.email = email;
    }
    setRole(role) {
        this.role = role;
    }
    setPrivate(isPrivate) {
        this.isPrivate = isPrivate;
    }
    getRole() {
        return this.role;
    }
    getDisplayName() {
        return this.displayName;
    }
    getUserTag() {
        return this.userTag;
    }
    isPrivate() {
        return this.isPrivate;
    }
    toString() {
        return `User: ${this.userTag} (${this.fullName})`;
    }
    static fromAPI(response) {
        return new User(
            response.id,
            response.userTag,
            response.email,
            response.displayName,
            response.role,
            response.createdAt,
            response.isPrivate
        );
    }
}