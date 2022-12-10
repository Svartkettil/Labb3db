import java.sql.*;
import java.util.Scanner;

public class Main {

    final private static Scanner scanner = new Scanner(System.in);
    private static Connection conn = null;

    private static Connection connect() {
        String url = "jdbc:sqlite:C:\\Users\\mauri\\DataGripProjects\\Database\\identifier.sqlite";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void printActions() {
        System.out.print("""
                Välkommen till film:
                e - Stäng av programmet
                0 - Visa alla filmer
                1 - Lägg till en film
                2 - Uppdatera en film
                3 - Lägg till genre
                4 - Uppdatera genre
                5 - Antal filmer tillgängliga
                6 - Radera en film
                7 - Radera en genre
                8 - Sök film efter regissör
                9 - Visa alla alternativ""");
    }


    private static void deleteMovieInput(){
        System.out.println("Skriv in id:t på filmen som ska tas bort: ");
        String inputId = scanner.nextLine();
        deleteMovie(Integer.parseInt(inputId));
    }

    private static void selectAll(){
        String sql = "SELECT film.filmId, film.filmTitel, film.regissor, film.biljettPris, genre.genreNamn FROM film " +
                "LEFT JOIN genre ON film.filmGenre = genre.genreId;";

        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println(("ID: ") + rs.getInt("filmId") +  "\t" +
                        ("Titel: ") + rs.getString("filmTitel") + "\t" +
                        ("Regissör: ") + rs.getString("regissor") + "\t" +
                        ("Pris: ") + rs.getString("biljettPris") + "\t" +
                        ("Genre: ") + rs.getString("genreNamn"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertMovieInput(){
        System.out.println("Skriv in filmtitel: ");
        String titel = scanner.nextLine();
        System.out.println("Skriv in regissör: ");
        String regissor = scanner.nextLine();
        System.out.println("Skriv in pris för biljett: ");
        String pris = scanner.nextLine();
        System.out.println("Välj genre: ");
        int genreId = choiceOfGenre();
        insert(titel, regissor, Integer.parseInt(pris), genreId);
    }

    private static int choiceOfGenre() {
        printListOfGenres();
        return Integer.parseInt(scanner.nextLine());
    }

    private static void printListOfGenres() {
        String sql = "SELECT * FROM genre";

        try{
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                System.out.println(rs.getString("genreId") + "\t" +
                        (rs.getString("genreNamn")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String titel, String regissor, int pris, int genre) {
        String sql = "INSERT INTO film(filmTitel, regissor, biljettPris, filmGenre) VALUES(?,?,?,?)";

        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titel);
            pstmt.setString(2, regissor);
            pstmt.setInt(3, pris);
            pstmt.setInt(4, genre);
            pstmt.executeUpdate();
            System.out.println("Du har lagt till en ny film");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateMovieInput(){
        System.out.println("Ange id på den film du vill införa ändringarna på: ");
        String id = scanner.nextLine();
        System.out.println("Ange titel: ");
        String titel = scanner.nextLine();
        System.out.println("Ange regissör: ");
        String regissor = scanner.nextLine();
        System.out.println("Ange pris för biljett: ");
        String pris = scanner.nextLine();
        System.out.println("Välj genre: ");
        int genre = choiceOfGenre();
        updateMovie(Integer.parseInt(id), titel, regissor, Integer.parseInt(pris), genre);
    }

    private static void updateMovie(int id, String titel, String regissor, int pris, int genre) {
        String sql = "UPDATE film SET filmTitel = ? , "
                + "regissor = ? , "
                + "biljettPris = ? ,"
                + "filmGenre = ?"
                + "WHERE filmId = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titel);
            pstmt.setString(2, regissor);
            pstmt.setInt(3, pris);
            pstmt.setInt(4, genre);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat vald film");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteMovie(int id) {
        String sql = "DELETE FROM film WHERE filmId = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort filmen");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchByDirectorInput() {
        System.out.println("Ange namnet på regissören: ");
        String director = scanner.nextLine();
        searchByDirector(director);
    }

    private static void searchByDirector(String director) {
        String sql = "SELECT filmTitel FROM film WHERE regissor = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, director);
            ResultSet rs = pstmt.executeQuery();
            int n = 1;
            while (rs.next()) {
                System.out.println("Titel "+ n + ": " + rs.getString("filmTitel"));
                n++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    private static void deleteGenreInput() {
        printListOfGenres();
        System.out.println("Skriv in id på genren som ska tas bort: ");
        int inputId = Integer.parseInt(scanner.nextLine());
        deleteGenre(inputId);
    }

    private static void deleteGenre(int id) {
        String sql = "DELETE FROM genre WHERE genreId = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort genren");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateGenreInput() {
        System.out.println("Ange id på den genre du vill uppdatera: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Vad ska genren heta istället?");
        String genre = scanner.nextLine();
        updateGenre(id, genre);

    }

    private static void updateGenre(int id, String genre) {
        String sql = "UPDATE genre SET genreNamn = ? WHERE genreId = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genre);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat vald genre");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addGenreInput() {
        System.out.println("Ange namn på den nya genren: ");
        String genre = scanner.nextLine();
        addGenre(genre);
    }

    private static void addGenre(String genre) {
        String sql = "INSERT INTO genre (genreNamn) VALUES (?)";

        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genre);
            pstmt.executeUpdate();
            System.out.println("Du har lagt till en ny genre");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    private static void movieCounter() {
        String sql = "SELECT COUNT(filmId) AS filmer FROM film";
            try {
                Connection conn = connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql);
                System.out.println(rs.getString(1));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        boolean quit = false;
        printActions();
        while(!quit) {
            System.out.println("\nVälj (9 för att visa val):");
            String action = scanner.nextLine();
            switch (action) {
                case "e" -> {
                    System.out.println("\nStänger ner...");
                    quit = true;
                }
                case "00" -> printListOfGenres();
                case "0" -> selectAll();
                case "1" -> insertMovieInput();
                case "2" -> updateMovieInput();
                case "3" -> addGenreInput();
                case "4" -> updateGenreInput();
                case "5" -> movieCounter();
                case "6" -> deleteMovieInput();
                case "7" -> deleteGenreInput();
                case "8" -> searchByDirectorInput();
                case "9" -> printActions();
            }
        }

    }

}
