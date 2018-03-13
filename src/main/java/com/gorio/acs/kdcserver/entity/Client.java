package com.gorio.acs.kdcserver.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Class Name Client
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/24
 */
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Client implements Serializable {
    private  int userIdentification;
    private @NonNull
    String masterSharedKey;
    private @NonNull
    String sessionKey;
}
