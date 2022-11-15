package Onol.onol.Domain.auth;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * MemberAuth 설명 : 회원들의 권한을 관리.
 */
@RequiredArgsConstructor
public enum MemberAuth {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String abbreviation;

    private static final Map<String, MemberAuth> lookup = new HashMap<>();

    static{
        for(MemberAuth auth : MemberAuth.values()){
            lookup.put(auth.abbreviation, auth);
        }
    }

    public String getAbbreviation(){
        return this.abbreviation;
    }

    public static MemberAuth get(String abbreviation){
        return lookup.get(abbreviation);
    }

    public static boolean containsKey(String abbreviation){
        return lookup.containsKey(abbreviation);
    }
}
