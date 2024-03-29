package app;

import utils.FileUtils;
import java.time.*;
import java.util.ArrayList;
import java.io.*;
import static utils.FileUtils.readCSV;

/**
 *
 * @author Frantisek Rehak
 */
public class BetCompany implements IBetCompany{
    /**
     * This is class representing BetCompany
     */
    private String name;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Bet> availableBets = new ArrayList<>();
    private ArrayList<Money> money = new ArrayList<>();
    public BetCompany(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addUser(User user) {
        /**
         * This method add User to ArrayList users, then it will write all users to file.
         * @param user User who will be added
         */
        this.users.add(user);
        FileUtils.appendToFile("data//login.csv",user.toString());
    }

    public void bankMoney() throws IOException {
        /**
         * This method write all bank accounts to file.
         */
        String s = "";
        for(Money money2 : this.money)
        {
            s += money2.toString()+"\n";
        }
        FileUtils.rWFile("bankAccounts.csv",s);
    }

    public void updateUsers() throws IOException {
        /**
         * This method write all users to file.
         *
         */
        String u = "";
        int val = 0;
        int max = this.users.size();
        for(User user : this.users){
            if(max-1>val){
                u += user.toString()+"\n";
            }else{
                u += user.toString();
            }
            val++;
        }
        FileUtils.rWFile("data//login.csv",u);
    }

    public void loadUsers() throws FileNotFoundException {
        /**
         * This method read all users from file, then add them to ArrayList users.
         */
        String[] lines = readCSV("data//login.csv");
        for (String line : lines) {
            String[] parm = line.split(";");
            User user = new User(parm[0], parm[2], Integer.parseInt(parm[1]), parm[3], Integer.parseInt(parm[4]),Float.parseFloat(parm[5]));
            this.users.add(user);
        }
    }

    // TODO vyjimka nepreskocit osetrenim vstupu/neexistence souboru

    public boolean checkUsername(String username){
        /**
         * This method check if username exists in ArrayList users.
         * @param username Username we are looking for.
         * @return Return true or false depends if username is found.
         */
        for(User user : this.users){
            if(user.getUsername().equals(username)){
                //System.out.println("User exists");
                return false;
            }
        }
        return true;
    }

    public boolean checkLogin(String username, String password){
        /**
         * This method check if all login parameters are valid.
         * @param username Username what is checked.
         * @param password Password what is checked.
         * @return Return true or false depends if login values are valid.
         */
        for(User user : this.users){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    public boolean checkCardnumber(String cardnumber){
        /**
         * This method check if credit card number is valid.
         * @param cardnumber Credit card number what is checked.
         * @return Return true or false depends if credit card number is found.
         */
        for(User user : this.users){
            if(user.getCardnumber().equals(cardnumber)){
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username){
        /**
         * This method return user by username.
         * @param username Username as key when finding user in ArrayList user.
         * @return return instance of class User.
         */
        for(User user : this.users){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    public void loadBets() throws FileNotFoundException {
        /**
         * This method read all bets from file, then add them to ArrayList bets.
         */
        String[] bets = readCSV("betData.csv");
        for(String bet : bets){
            String[] parm = bet.split(";");
            Bet bet2 = new Bet(parm[0],parm[1],Float.parseFloat(parm[2]),Float.parseFloat(parm[3]),Float.parseFloat(parm[4]), LocalDate.parse(parm[5]), LocalTime.parse(parm[6]));
            this.availableBets.add(bet2);
        }
    }

    public void loadMoney() throws FileNotFoundException {
        /**
         * This method read all bank accounts from file, then add them to ArrayList money.
         */
        String[] moneyS = readCSV("bankAccounts.csv");
        for(String money : moneyS){
            String[] parm = money.split(";");
            Money money1 = new Money(parm[0],Float.parseFloat(parm[1]),Boolean.parseBoolean(parm[2]));
            this.money.add(money1);
        }
    }

    public float getMoneyByCardnumber(String cardnumber, String username){
        /**
         * This method get money from bank account found by credit car number.
         * @param cardnumber Credit card number as key when finding user in ArrayList money.
         * @return Return amount of balance in this bank account.
         */
        for(Money money1 : this.money){
            if(money1.getcardnumber().equals(cardnumber) && money1.getStatus() == false){
                money1.setStatus(true);
                return money1.getMoney();
            }
        }
        return 0;
    }

    public Bet getBetByIndex(int rowNumber){
        /**
         * This method get bet by index in ArrayList bets
         * @param rowNumber Number as index for ArrayList bets
         * @return Return instance of class Bet
         */
        return this.availableBets.get(rowNumber-1);
    }

    public String readBinaryResults() throws IOException{
        StringBuilder sb = new StringBuilder();
        try(DataInputStream in = new DataInputStream(new FileInputStream("betsBinary"))){
            boolean end = false;
            int nUsers, nTickets, tip, money, rank;
            String name, homeTeam, awayTeam, status;
            nUsers = in.readInt();
            while(!end){
                try{
                    for(int i = 0; i < nUsers; i++){
                        nTickets = in.readInt();
                        name = in.readUTF();
                        sb.append(String.format("\nJmeno: %s%n", name));
                        sb.append(String.format("Pocet ticketu: "));
                        sb.append(String.format("%d \n", nTickets));
                        rank = 1;
                        for(int j = 0; j < nTickets; j++){
                            homeTeam = in.readUTF();
                            awayTeam = in.readUTF();
                            status = in.readUTF();
                            tip = in.readInt();
                            money = in.readInt();
                            sb.append(String.format("%2d. %s  vs  %s  %s  %d %d %n", rank, homeTeam, awayTeam, status, tip, money));
                            rank++;
                        }
                    }
                    sb.append("\n");
                } catch (EOFException e){
                    end = true;
                }
            }
        }
        return sb.toString();
    }

    public void getBinaryData() throws IOException {
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream("betsBinary",false))) {
            out.writeInt(users.size());
            for (User user : this.users) {
                user.loadTickets();
                user.saveToBinary();
            }
            System.out.println(readBinaryResults());
        }
    }

    public String toStringBets() {
        /**
         * This method print all available bets
         */
        StringBuilder bs = new StringBuilder();
        int row = 1;
        for (Bet bet: this.availableBets){
            bs.append(row+") "+bet.toString()).append("\n");
            row++;
        }
        return bs.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (User user: this.users) {
            sb.append(user.toString()).append("\n");
        }
        return sb.toString();
    }
}
