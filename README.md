Requisitos Mínimos e Instruções de Execução

Grupo: Vitor Pires, Gustavo Aragão, Pedro Affonso, Luiz Rosário.

Este documento fornece informações sobre os requisitos mínimos e as instruções necessárias para compilar e executar a aplicação cliente-servidor de um jogo de Forca Multijogador em Java.

Requisitos Mínimos

Software

1. Java Development Kit (JDK) 8 ou superior
* É necessário instalar o JDK para compilar e executar o código Java.

2\. IDE de Desenvolvimento Java (opcional)

* Recomenda-se o uso de uma IDE como Eclipse, IntelliJ IDEA, ou NetBeans para facilitar o desenvolvimento e a execução.

3\. Sistema Operacional

* Windows, macOS ou Linux.

Hardware

1. Memória RAM: Mínimo de 2 GB
1. Espaço em Disco: Mínimo de 100 MB para JDK e arquivos de projeto.
1. Processador: CPU com suporte a instruções Java.

Instruções para Compilação e Execução

Passo 1: Instalação do JDK

1. Baixe e instale o JDK a partir do site oficial da Oracle ou utilize um JDK de código aberto como o OpenJDK.
1. Verifique a instalação do JDK abrindo um terminal e digitando:
1. java -version

3\. Você deve ver a versão do Java instalada.

Passo 2: Configuração do Projeto

1. Clone o repositório do GitHub para o seu ambiente local:

git clone https://github.com/v1tor2003/ForcaJAVA

1. Navegue até o diretório do projeto:

cd ForcaJAV


Passo 3: Compilação do Código

1. Navegue até o diretório onde os arquivos .java estão localizados:

cd ForcaJAVA

1. Compile os arquivos Java utilizando o javac:

javac \*.java

Passo 4: Execução do Servidor

1. Após a compilação, execute o servidor:

java Server

1. O servidor deve iniciar e exibir uma mensagem indicando que está aguardando conexões de clientes.

Passo 5: Execução do Cliente

1. Em um novo terminal ou console, navegue até o diretório do projeto novamente:

cd ForcaJAVA

1. Execute o cliente:

java Client

1. Siga as instruções na interface do cliente para criar ou entrar em uma sala e participar do jogo.

Estrutura do Projeto

ForcaJAVA/

├── .gitignore

├── Client.java

├── IClientAction.java

├── IServerAction.java

├── README.md

├── Room.java

├── Server.java

├── Utils.java


Client.java: Implementa a lógica do cliente que interage com o jogador.

IClientAction.java: Interface funcional para ações executadas no cliente.

IServerAction.java: Interface funcional para ações executadas no servidor.

Room.java: Implementa a lógica de uma sala de jogo, incluindo jogadores, palavras, e estado do jogo.

Server.java: Implementa a lógica do servidor que gerencia o jogo.

Utils.java: Fornece utilitários como interpretação de protocolo e limpeza de console.

.gitignore: Ignora arquivos compilados, logs e outros artefatos não essenciais para controle de versão no projeto de jogo multiplayer de Forca em Java.

README.md: Documenta a instalação, uso e configuração do jogo multiplayer de Forca em Java, incluindo detalhes sobre funcionalidades e estrutura do projeto. É ele que você está lendo.


Notas Adicionais

* Certifique-se de que o servidor esteja em execução antes de iniciar qualquer cliente.
* A comunicação entre o cliente e o servidor é baseada no protocolo TCP/IP.
* Para encerrar o servidor ou cliente, utilize a combinação de teclas Ctrl+C no terminal.

Troubleshooting

* Erro de Porta Já em Uso: Certifique-se de que a porta utilizada pelo servidor (por padrão, 12345) não está sendo usada por outra aplicação. Você pode mudar a porta no código do servidor, se necessário.
* Problemas de Conexão: Verifique se o firewall está bloqueando a comunicação entre o cliente e o servidor. Desative temporariamente o firewall para testar a conexão.

Com essas instruções, você deverá ser capaz de configurar, compilar e executar a aplicação de forma eficiente.

Protocolo de Aplicação

Este documento descreve o protocolo de comunicação entre cliente e servidor para um jogo de Forca Multijogador. O protocolo é utilizado para troca de mensagens que controlam a criação de salas, a entrada de jogadores, a adivinhação de letras, e outras ações relacionadas ao jogo.

Mensagens do Cliente para o Servidor

1. CREATE\_ROOM
* Formato: CREATE\_ROOM <player\_name> <word>
* Descrição: Cria uma nova sala de jogo.
* Parâmetros:
* player\_name: Nome do jogador que está criando a sala.
* word: Palavra que será utilizada no jogo.

2\. JOIN\_ROOM

* Formato: JOIN\_ROOM <room\_code> <player\_name>
* Descrição: Entra em uma sala de jogo existente.
* Parâmetros:
* room\_code: Código da sala para onde o jogador deseja entrar.
* player\_name: Nome do jogador que está entrando na sala.

3\. GUESS

* Formato: GUESS <letter>
* Descrição: Envia uma letra para tentar adivinhar a palavra.
* Parâmetros:
* letter: Letra que o jogador deseja adivinhar.

4\. START\_GAME

* Formato: START\_GAME <word>
* Descrição: O dono da sala inicia o jogo.
* Parâmetros:
* word: Palavra que será utilizada no jogo.

5\. LIST\_ROOMS

* Formato: LIST\_ROOMS
* Descrição: Pedido para listar as salas de jogo existentes.

6\. DISCONNECT

* Formato: DISCONNECT
* Descrição: O jogador desconecta do servidor.

7\. CLOSE\_ROOM

* Formato: CLOSE\_ROOM
* Descrição: O dono da sala fecha a sala de jogo.

Mensagens do Servidor para o Cliente

1. GAME\_STARTED
* Formato: GAME\_STARTED
* Descrição: Notifica que o jogo começou.

2\. ROOM\_CLOSED

* Formato: ROOM\_CLOSED
* Descrição: Notifica que a sala de jogo fechou.

3\. FULL\_ROOM

* Formato: FULL\_ROOM
* Descrição: Notifica que a sala está cheia e o jogador não pode entrar.

4\. ENTERED\_ROOM

* Formato: ENTERED\_ROOM
* Descrição: Notifica que o jogador se uniu com sucesso a sala.

5\. INVALID\_ROOM

* Formato: INVALID\_ROOM
* Descrição: Notifica que a sala solicitada não existe.

6\. SERVER\_LOG

* Formato: SERVER\_LOG <message>
* Descrição: Envia mensagens de log do servidor para o cliente.
* Parâmetros:
* message: Mensagem de log a ser exibida.

7\. START\_TURN

* Formato: START\_TURN
* Descrição: Notifica que é a vez do jogador adivinhar uma letra.

8\. END\_TURN

* Formato: END\_TURN
* Descrição: Notifica que a vez do jogador terminou.

9\. GAME\_OVER

* Formato: GAME\_OVER
* Descrição: Notifica que o jogo terminou.

Fluxo de Mensagens

1. Criação de Sala:
* Cliente: CREATE\_ROOM
* Servidor: SERVER\_LOG <room\_info>

2\. Entrada em Sala:

* Cliente: JOIN\_ROOM <room\_code>
* Servidor: ENTERED\_ROOM
* Servidor: SERVER\_LOG <room\_info>

3\. Início de Jogo:

* Cliente: START\_GAME <word>
* Servidor: GAME\_STARTED

4\. Adivinhar Letra:

* Servidor: SET\_TURN
* Client: GUESS <word>
* Servidor: END\_TURN

5\. Listar Salas:

* Cliente: LIST\_ROOMS
* Servidor: SERVER\_LOG <rooms\_info>

6\. Desconexão:

* Cliente: DISCONNECT

7\. Fechar Sala:

* Cliente: CLOSE\_ROOM
* Servidor: SERVER\_LOG <info>

8\. Fim de Jogo:

* Servidor: GAME\_OVER
* Cliente: CLOSE\_ROOM

Este protocolo define claramente as interações entre cliente e servidor, assegurando uma comunicação eficiente e consistente para a gestão do jogo de Forca Multijogador.

Motivação para uso do TCP como protocolo de transporte

O uso de TCP no jogo da forca multijogador é crucial para garantir a confiabilidade da comunicação entre o servidor e os jogadores. Diferente do UDP, o TCP assegura que todos os pacotes de dados cheguem ao destino sem perdas e na ordem correta, o que é vital para manter a consistência do estado do jogo, sincronização das tentativas de adivinhação e comunicação crítica, como criação de salas e entradas de jogadores. A perda de pacotes em um protocolo não confiável como o UDP resultaria em uma experiência de jogo inconsistente e frustrante para os jogadores.


Resumo do funcionamento do jogo

No jogo da forca multijogador, um jogador cria uma sala e define uma palavra secreta. Pelo menos um e no máximo três outros jogadores entram para adivinhar a palavra, enquanto o criador observa. Os jogadores têm um limite de seis erros. O estado do jogo é exibido após cada tentativa. O jogo termina quando a palavra é adivinhada ou o limite de erros é atingido, e o criador decide se continua com uma nova palavra ou fecha a sala.
