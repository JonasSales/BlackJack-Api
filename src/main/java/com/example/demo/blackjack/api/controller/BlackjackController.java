    package com.example.demo.blackjack.api.controller;

    import com.example.demo.auth.dto.UserDTO;
    import com.example.demo.auth.service.AuthenticationService;
    import com.example.demo.auth.service.UserService;
    import com.example.demo.blackjack.api.DTO.playerRequest;
    import com.example.demo.blackjack.model.Card;
    import com.example.demo.blackjack.model.Player;
    import com.example.demo.blackjack.domain.service.BlackjackGameService;
    import com.example.demo.blackjack.model.Table;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/blackjack")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public class BlackjackController {

        private final BlackjackGameService gameFunctions;
        private final UserService userService;
        private final AuthenticationService authenticationService;
        private final Map<UUID, Table> mesas = new HashMap<>(); // Array de mesas

        public BlackjackController(BlackjackGameService gameFunctions, UserService userService, AuthenticationService authenticationService) {
            this.gameFunctions = gameFunctions;
            this.userService = userService;
            this.authenticationService = authenticationService;
        }

        // Listar jogadores de uma mesa
        @GetMapping("/mesas/{mesaId}/jogadores")
        public ResponseEntity<List<Player>> jogadores(@PathVariable UUID mesaId) {
            Table mesa = gameFunctions.getMesas().get(mesaId);
            if (mesa == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Player> jogadores =  mesa.getJogadores();
            return new ResponseEntity<>(jogadores, HttpStatus.OK);
        }

        // Jogador Atual de uma mesa
        @GetMapping("/mesas/{mesaId}/jogadoratual")
        public ResponseEntity<List<Player>> jogadoresAtuais(@PathVariable UUID mesaId) {
            Table mesa = mesas.get(mesaId);
            if (mesa == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Player> jogadoresAtuais = mesa.getJogadores().stream()
                    .filter(player -> player.isJogadorAtual() && !player.isPerdeuTurno() && !player.isStand())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(jogadoresAtuais, HttpStatus.OK);
        }

        // Adicionar jogador à mesa
        @PostMapping("/mesas/{mesaId}/adicionar")
        public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
            UserDTO userDTO = userService.getUserFromToken(request);
            Player jogador = new Player(userDTO);
            Map<String, String> response = new HashMap<>();
            Table mesa = mesas.get(mesaId);
            if (mesa == null) {
                response.put("message", "Mesa não encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            boolean sucesso = mesa.adicionarJogador(jogador);
            response.put("message", sucesso ? "Jogador " + jogador.getUser().getName() +
                    " adicionado à mesa!" : "Não foi possível adicionar " + jogador.getUser().getName() + ".");
            return new ResponseEntity<>(response, sucesso ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        }

        // Iniciar jogo na mesa
        @PostMapping("/mesas/{mesaId}/iniciar")
        public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
            Map<String, String> response = new HashMap<>();
            Table mesa = mesas.get(mesaId);
            if (mesa == null) {
                response.put("message", "Mesa não encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            if (mesa.getJogadores().size() < 2) {
                response.put("message", "É necessário pelo menos dois jogadores para iniciar o jogo!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            mesa.iniciarJogo();
            response.put("message", "Jogo iniciado! Cartas distribuídas!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Cartas de uma mesa
        @GetMapping("/mesas/{mesaId}/cartas")
        public ResponseEntity<Map<String, List<String>>> getCartasDeJogadores(@PathVariable UUID mesaId) {
            Table mesa = mesas.get(mesaId);
            if (mesa == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            Map<String, List<String>> cartasPorJogador = mesa.getJogadores().stream()
                    .collect(Collectors.toMap(
                            player -> player.getUser().getName(),
                            player -> player.getMao().stream()
                                    .map(Card::toString)
                                    .collect(Collectors.toList())
                    ));
            return new ResponseEntity<>(cartasPorJogador, HttpStatus.OK);
        }

        // Jogada na mesa
        @PostMapping("/mesas/{mesaId}/jogada")
        public ResponseEntity<String> realizarJogada(@PathVariable UUID mesaId, HttpServletRequest request, @RequestBody playerRequest jogada) {
            UserDTO userDTO = userService.getUserFromToken(request);
            Player jogador = new Player(userDTO);

            boolean jogadaValida = gameFunctions.jogada(jogador, jogada.getJogada(), mesaId);
            if (!jogadaValida) {
                String errorMessage = "Jogada inválida para " + jogador.getUser().getName();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            if (gameFunctions.verificarTodosEncerraram(mesaId)) {
                gameFunctions.finalizarJogo(mesaId);
                String finalMessage = "Todos os jogadores encerraram a mão. O jogo foi finalizado.";
                return ResponseEntity.ok(finalMessage);
            }
            Player proximoJogador = gameFunctions.proximoJogador(mesaId);
            if (proximoJogador == null) {
                gameFunctions.finalizarJogo(mesaId);
                String finalMessage = "Não há jogadores válidos restantes. O jogo foi finalizado.";
                return ResponseEntity.ok(finalMessage);
            }
            String nextMessage = "Próximo jogador: " + proximoJogador.getUser().getName();
            return ResponseEntity.ok(nextMessage);
        }

        @GetMapping("/partidaIniciada/{mesaId}")
        public ResponseEntity<Map<String, String>> partidaIniciada(@PathVariable UUID mesaId) {
            Map<String, String> response = new HashMap<>();

            Table mesa = gameFunctions.encontrarMesaPorId(mesaId);
            if (mesa == null) {
                response.put("jogoIniciado", "false");
                return ResponseEntity.ok(response);
            }
            response.put("jogoIniciado", String.valueOf(mesa.getJogoIniciado()));
            return ResponseEntity.ok(response);
        }

        @PostMapping("/criarMesa")
        public ResponseEntity<String> criarMesa(HttpServletResponse response) {
            Table mesa = gameFunctions.criarNovaMesa();
            mesas.put(mesa.getId(), mesa);
            String mesaToken = authenticationService.gerarTokenMesa(mesa.getId(), response);
            return ResponseEntity.ok(mesaToken);
        }


        @GetMapping("/mesa/{mesaId}")
        public ResponseEntity<?> acessarMesa(@PathVariable UUID mesaId, @RequestHeader("Authorization") String token) {
            UUID mesaIdFromToken = authenticationService.validarTokenMesa(token);
            if (mesaIdFromToken == null || !mesaIdFromToken.equals(mesaId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido ou expirado"));
            }

            Table mesa = mesas.get(mesaId);
            if (mesa == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Mesa não encontrada"));
            }

            return ResponseEntity.ok(Map.of(
                    "mesaId", mesa.getId(),
                    "jogoIniciado", mesa.getJogoIniciado(),
                    "jogadores", mesa.getJogadores()
            ));
        }

        @GetMapping("/mesas")
        public ResponseEntity<List<Map<String, Object>>> listarMesas() {
            List<Map<String, Object>> mesasComSub = mesas.values().stream()
                    .map(mesa -> {
                        Map<String, Object> mesaInfo = new HashMap<>();
                        mesaInfo.put("mesaId", mesa.getId());// Acredito que o "sub" se refira ao criador da mesa
                        return mesaInfo;
                    })
                    .collect(Collectors.toList());

            if (mesasComSub.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mesasComSub);
            }

            return ResponseEntity.ok(mesasComSub);
        }

    }
