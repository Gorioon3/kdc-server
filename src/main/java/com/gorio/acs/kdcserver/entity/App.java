package com.gorio.acs.kdcserver.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Class Name App
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/24
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PUBLIC)
@ToString
public class App  implements Serializable{
    /**
     * applicationIdentification
     * 应用ID
     * int
     * primary key
     * Not Null
     * Unique
     * Auto Increment
     */
    private  int applicationIdentification;
    private @NonNull String applicationUniqueIdentifier;
    private @NonNull String rsaPublicKey;
}
