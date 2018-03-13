package com.gorio.acs.kdcserver.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Class Name Server
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PUBLIC)@Getter
@ToString
public class Server implements Serializable{
    private int serverIdentification;
    private String masterSharedKey;
    private String serverIP;
}
