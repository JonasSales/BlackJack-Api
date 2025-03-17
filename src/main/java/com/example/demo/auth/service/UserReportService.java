package com.example.demo.auth.service;

import com.example.demo.auth.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

@Service
public class UserReportService {

    public File generateUserReport(UserDTO user, String directoryPath) throws IOException {
        // Cria o diretório temporário, se não existir
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Cria o arquivo temporário
        File file = new File(directoryPath + "/user_report_" + user.getId() + ".txt");

        // Calcula estatísticas
        int partidasGanhas = user.getPartidasGanhas();
        int partidasTotais = user.getPartidasTotais();
        int partidasPerdidas = partidasTotais - partidasGanhas;
        double porcentagemVitorias = (partidasTotais > 0) ? ((double) partidasGanhas / partidasTotais) * 100 : 0;
        double porcentagemDerrotas = (partidasTotais > 0) ? ((double) partidasPerdidas / partidasTotais) * 100 : 0;

        // Formata os valores monetários e porcentagens
        DecimalFormat df = new DecimalFormat("#.##");
        String moneyFormatted = df.format(user.getMoney());
        String porcentagemVitoriasFormatted = df.format(porcentagemVitorias);
        String porcentagemDerrotasFormatted = df.format(porcentagemDerrotas);

        // Escreve as informações no arquivo
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("=== Relatório do Usuário ===\n");
            writer.write("ID: " + user.getId() + "\n");
            writer.write("Nome: " + user.getName() + "\n");
            writer.write("Email: " + user.getEmail() + "\n");
            writer.write("Partidas Totais: " + partidasTotais + "\n");
            writer.write("Partidas Ganhas: " + partidasGanhas + "\n");
            writer.write("Partidas Perdidas: " + partidasPerdidas + "\n");
            writer.write("Porcentagem de Vitórias: " + porcentagemVitoriasFormatted + "%\n");
            writer.write("Porcentagem de Derrotas: " + porcentagemDerrotasFormatted + "%\n");
            writer.write("Dinheiro: $" + moneyFormatted + "\n");
            writer.write("===========================\n");
        }

        return file;
    }
}