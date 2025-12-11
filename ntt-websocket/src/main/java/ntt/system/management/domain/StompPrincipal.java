/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : Custom Principal class which is used for anonymous user sessions in Websocket connection
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.system.management.domain;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

@Getter
@Setter
public class StompPrincipal implements Principal {
    private String name;
    private String id;

    public StompPrincipal(String id, String name) {
        this.id = id;
        this.name = name;
    }


}