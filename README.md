# FIAP | Sistema de Ponto

## Arquitetura da solução

![hackathon](https://github.com/galenodemelo/fiap-hackathon-sistema-de-ponto/assets/10313123/c5bdc528-253c-4e10-8cb1-0817f35733ad)

### Autenticação

O processo de autenticação espera receber as credenciais do usuário no formato Basic (Base64 de usuario:senha).

Os dados recebidos são confrontados com a base de dados (RDS), validando as credenciais informadas.

As demais ações dependem da autorização bem sucedida do usuário.

### Registro de Ponto

O registro de marcação de ponto é realizado através de uma requisição POST ao respectivo endpoint, recebendo no corpo da requisição o atributo que representa o evento da marcação. Exemplo:

```json
{
  "event": "ENTRY"
}
```

As opções de evento aceitas são:
- `ENTRY`: Entrada 
- `INTERVAL_BEGIN`: Início do intervalo
- `INTERVAL_END`: Fim do intervalo
- `EXIT`: Saída

Ao salvar o registro, o serviço identifica o horário atual, bem como o usuário autenticado para persistir os dados no banco de dados (RDS).

### Listar Marcações

O usuário solicita as informações através de requisição GET ao respectivo endpoint. O serviço identifica o usuário autenticado e consulta as marcações realizadas no período de 30 dias retroativos no banco de dados (RDS).

O resultado é apresentado no formato JSON, indicando as datas em que o usuário registrou marcação, relacionando as marcações e os tipos de marcações realizadas. Por fim, para cada data informa o total de horas trabalhadas no respectivo dia, conforme exemplo:

```json
{
  "dates": [
    {
      "date": "18/03/2024",
      "punches": [
        {
          "punch": "08:00",
          "event": "ENTRY"
        },
        {
          "punch": "12:00",
          "event": "INTERVAL_BEGIN"
        },
        {
          "punch": "13:00",
          "event": "INTERVAL_END"
        },
        {
          "punch": "17:00",
          "event": "EXIT"
        }
      ],
      "total": "08:00"
    }
  ]
}
```

### Solicitar Espelho de Ponto

A solicitação do espelho de ponto se dá através de requisição POST ao devido endpoint, informando o período a ser utilizado na geração do relatório, conforme exemplo abaixo:

```json
{"startDate": "2024-01-01", "endDate": "2024-01-31"}
```

O período informado é validado de acordo com as regras:
- A data inicial não pode ser maior que a data atual
- A data inicial não pode ser maior que a data final.

Sendo uma requisição válida, a solicitação de geração de espelho de ponto é armazenada em uma fila SQS para posterior processamento. A mensagem armazenada na fila possui a estrutura definida de acordo com:

```json
{"email": "user@email.com", "startDate": "2024-01-01", "endDate": "2024-01-31"}
```

### Geração do Espelho de Ponto

A geração do espelho de ponto é um processo que não necessita da requisição do usuário, pois a inclusão da solicitação na fila SQS, gerada na etapa anterior, dispara a execução desse serviço.

Ao consumir a fila, o processo realiza a geração do arquivo em formato CSV e o armazena em um bucket S3, gerando uma URL pré-assinada.

A partir da geração do arquivo, um e-mail é enviado ao solicitante do espelho de ponto, contendo a URL pré-assinada associada ao arquivo, permitindo o seu download.

## Banco de dados

O serviço de banco de dados escolhido foi o AWS RDS. Essa escolha se deu em virtude das vantagens em se utilizar um banco de dados relacional, dentre as quais os pontos determinantes foram: integridade referencial e segurança.

O modelo de dados conta com 2 tabelas, conforme modelo abaixo:

![hackathon-er](https://github.com/galenodemelo/fiap-hackathon-sistema-de-ponto/assets/10313123/cf5e0cc1-a0f1-4afa-848b-77c9d879a154)

<details>
  <summary>Scripts de criação das tabelas</summary>

```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
);

CREATE TABLE `punch` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `event` varchar(255) NOT NULL,
  `punch_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_ID` (`user_id`),
  CONSTRAINT `FK_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);
```

</details>






