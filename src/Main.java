import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Gson gson = new Gson();
        ConsomeApi api = new ConsomeApi();
        int cursor;
        int cep;
        boolean flag = true;
        Scanner scan = new Scanner(System.in);

        try (Connection connection = Banco.connect()) {
            while (flag) {
                System.out.println("1. Consultar Cep - 2.Listar Ceps Consultados - 3. Sair");
                cursor = scan.nextInt();
                if (cursor == 1) {
                    System.out.println("Digite um cep: ");
                    cep = scan.nextInt();
                    String resultado = api.retornaEnd(cep);
                    System.out.println(resultado);
                    Endereco endereco = gson.fromJson(resultado, Endereco.class);
                    System.out.println(endereco);
                    String dataHora = api.retornaDate();

                    try (FileWriter escrita = new FileWriter("log.txt", true)) {
                        escrita.write("CEP: " + cep + " | Data/Hora: " + dataHora + "\n");
                        System.out.println("Log salvo");
                    } catch (IOException e) {
                        System.out.println("Ocorreu um erro ao salvar o log.");
                        e.printStackTrace();
                    }

                    String sql = "INSERT INTO enderecos (cep, logradouro, complemento, bairro, localidade, uf, data_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, endereco.getCep());
                        stmt.setString(2, endereco.getRua());
                        stmt.setString(3, endereco.getComplemento());
                        stmt.setString(4, endereco.getBairro());
                        stmt.setString(5, endereco.getCidade());
                        stmt.setString(6, endereco.getEstado());
                        stmt.setTimestamp(7, java.sql.Timestamp.valueOf(dataHora));
                        stmt.executeUpdate();
                        System.out.println("Dados do endereço inseridos no banco de dados");
                    } catch (SQLException e) {
                        System.out.println("Ocorreu um erro ao inserir os dados do endereço no banco de dados.");
                        e.printStackTrace();
                    }
                } else if (cursor == 2) {
                    String sql = "SELECT cep, logradouro, complemento, bairro, localidade, uf, data_hora FROM enderecos";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        ResultSet rs = stmt.executeQuery();
                        System.out.println("Dados salvos no banco de dados:");
                        while (rs.next()) {
                            System.out.println("CEP: " + rs.getString("cep"));
                            System.out.println("Logradouro: " + rs.getString("logradouro"));
                            System.out.println("Complemento: " + rs.getString("complemento"));
                            System.out.println("Bairro: " + rs.getString("bairro"));
                            System.out.println("Localidade: " + rs.getString("localidade"));
                            System.out.println("UF: " + rs.getString("uf"));
                            System.out.println("Data/Hora: " + rs.getTimestamp("data_hora"));
                            System.out.println("--------------------------");
                        }
                    } catch (SQLException e) {
                        System.out.println("Ocorreu um erro ao listar os dados do banco de dados.");
                        e.printStackTrace();
                    }
                } else if (cursor == 3) {
                    flag = false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados.");
            e.printStackTrace();
        }
        scan.close();
    }
}
