package br.com.fiap.authorizer;

import br.com.fiap.user.User;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.SecurityUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;

import java.sql.SQLException;
import java.util.Base64;

public class AuthorizerHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, IamPolicyResponse> {

    @Override
    public IamPolicyResponse handleRequest(final APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent, final Context context) {
        final String invokedFunctionArn = apiGatewayCustomAuthorizerEvent.getMethodArn();

        try {
            String token = apiGatewayCustomAuthorizerEvent.getAuthorizationToken().trim();
            if (token.isEmpty()) throw new RuntimeException("Token não informado");

            String principalId = getPrincipalId(token);

            return AuthPolicyBuilder.allow(principalId, invokedFunctionArn);
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return AuthPolicyBuilder.deny(invokedFunctionArn);
        }
    }

    private static String getPrincipalId(String token) throws SQLException {
        if (!token.startsWith("Basic ")) throw new RuntimeException("Tipo de autenticação não suportado");

        String base64 = token.replace("Basic ", "");
        String decoded = new String(Base64.getDecoder().decode(base64));

        String[] tokenParts = decoded.split(":");
        if (tokenParts.length != 2) throw new RuntimeException("Token em formato inválido");

        String username = tokenParts[0];
        String password = tokenParts[1];

        User user = User.findByEmail(username);
        if (user == null || !SecurityUtils.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return user.getId().toString();
    }
}
