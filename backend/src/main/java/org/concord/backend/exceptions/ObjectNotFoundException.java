package org.concord.backend.exceptions;

import lombok.Data;

@Data
public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(){
        super("No object was found with the given id");
    }


}
