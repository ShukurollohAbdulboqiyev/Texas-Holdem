public class GameSetup {

    public Player createPlayer(String name, double chips) {
        return new Player(name, chips);
    }

    public Table createTable() {
        return new Table();
    }

    public void addPlayer(Table table, Player player) {
        table.addPlayer(player);
    }
}