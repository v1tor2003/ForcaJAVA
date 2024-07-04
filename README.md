# Jogo de Forca Multiplayer

Bem-vindo ao repositório do Jogo de Forca Multiplayer! Este projeto implementa um jogo de Forca onde vários jogadores podem participar simultaneamente através de sockets em Java.

## Funcionalidades

- Jogo multiplayer com suporte para vários jogadores.
- Comunicação via sockets.
- Interface de linha de comando para interação com o jogo.
- Gerenciamento de palavras e pontuações dos jogadores.
- Fácil de configurar e executar.

## Pré-requisitos

Antes de começar, certifique-se de ter o seguinte instalado em sua máquina:

- [Java JDK 8+](https://www.oracle.com/java/technologies/javase-downloads.html)

## Configuração e Execução

### Clonando o Repositório

Primeiro, clone este repositório para sua máquina local:

```bash
git clone https://github.com/v1tor2003/ForcaJAVA.git
cd ForcaJAVA
```

### Compilando o Projeto

Compile o projeto usando o compilador do Java:

```bash
javac *.java
```

### Executando o Servidor

Para iniciar o servidor do jogo, execute o seguinte comando:

```bash
java Server
```

### Executando o Cliente

Para iniciar um cliente do jogo, execute o seguinte comando:

```bash
java Client
```

## Estrutura do Projeto

- `Servidor.java`: Implementa a lógica do servidor que gerencia o jogo.
- `Cliente.java`: Implementa a lógica do cliente que interage com o jogador.
- 
## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests para melhorar este projeto.

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---