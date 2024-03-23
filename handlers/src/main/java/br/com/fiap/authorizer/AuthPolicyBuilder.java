package br.com.fiap.authorizer;

import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthPolicyBuilder {

    public static IamPolicyResponse allow(String principalId, String resource) {
        List<IamPolicyResponse.Statement> statementList = new ArrayList<>();
        statementList.add(IamPolicyResponse.allowStatement(resource));

        return IamPolicyResponse.builder()
                .withPrincipalId(principalId)
                .withPolicyDocument(buildPolicyDocument(statementList))
                .withContext(Map.of("principalId", principalId))
                .build();
    }

    public static IamPolicyResponse deny(String resource) {
        List<IamPolicyResponse.Statement> statementList = new ArrayList<>();
        statementList.add(IamPolicyResponse.denyStatement(resource));

        return IamPolicyResponse.builder()
                .withPrincipalId("user")
                .withPolicyDocument(buildPolicyDocument(statementList))
                .build();
    }

    private static IamPolicyResponse.PolicyDocument buildPolicyDocument(List<IamPolicyResponse.Statement> statementList) {
        return IamPolicyResponse.PolicyDocument.builder()
                .withVersion(IamPolicyResponse.VERSION_2012_10_17)
                .withStatement(statementList)
                .build();
    }
}
