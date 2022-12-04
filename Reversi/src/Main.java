import javax.xml.stream.FactoryConfigurationError;
import java.lang.constant.Constable;
import java.util.*;


// Не успел разобраться, как в intellij фрагментировать на несколько разных
// файлов для более удобного чтения, поэтому все классы в одном файле.

/*Класс представляющий игрока: и пользователя, и  компьютера*/
class Player {
    private boolean isFirst;
    private ArrayList<int[]> coordinates;
    private ArrayList<int[]> possibleCoordinates;

    Player(boolean isFirst) {
        this.isFirst = isFirst;
    }

    protected boolean makeMove(int[][] playingBoard) {
        coordinates = new ArrayList<>();
        possibleCoordinates = new ArrayList<>();
        if (isFirst) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (playingBoard[i][j] == -1) {
                        coordinates.add(new int[]{i, j});
                    }
                }
            }
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (playingBoard[i][j] == 1) {
                        checkingCells(i, j);
                    }
                }
            }
        } else {
            additionalMethod(playingBoard);
        }
        if (possibleCoordinates.isEmpty()) {
            System.out.println("Нет свободных клеток для игры.");
            return false;
        }
        System.out.print("Ходит игрок с " + (!isFirst ? "белыми" : "черными") + " фишками. ");
        System.out.println("Возможные квадраты для дальнейшей игры:");
        for (int[] coordinate : possibleCoordinates) {
            System.out.print(coordinate[0] + 1);
            System.out.print(" ");
            System.out.println(coordinate[1] + 1);
        }
        System.out.println("Введите только два целых значения слитно, например:45");
        int value = Menu.parseInt(11, 88);
        while (!parseCell(value)) {
            value = Menu.parseInt(11, 88);
        }
        changeChip(value / 10 - 1, value % 10 - 1, playingBoard);
        Game.printTable();
        Game.lastBoards.add(Menu.copy(playingBoard));
        return true;
    }
    private boolean parseCell(int value) {
        int first = value / 10 - 1, second = value % 10 - 1;
        boolean flag = false;
        for (int[] coordinate : possibleCoordinates) {
            if (coordinate[0] == first && coordinate[1] == second) {
                return true;
            }
        }
        return false;
    }
    // Реализация легкого метода действий компьютера
    protected boolean easyMode(int[][] playingBoard) {
        coordinates = new ArrayList<>();
        possibleCoordinates = new ArrayList<>();
        additionalMethod(playingBoard);
        if (possibleCoordinates.isEmpty()) {
            return false;
        }
        float max = 0,value;
        for(int[] coordinate : possibleCoordinates){
            value = Game.chipsFunction(coordinate[0],coordinate[1],coordinates);
            if(value > max){
                max = value;
            }
        }
        for(int[] coordinate : possibleCoordinates){
            if(max == Game.chipsFunction(coordinate[0],coordinate[1],possibleCoordinates)){
                changeChip(coordinate[0],coordinate[1],playingBoard);
                break;
            }
        }
        System.out.println("\nСтол после хода компьютера:");
        Game.lastBoards.add(Menu.copy(playingBoard));
        Game.printTable();
        return true;
    }

    private void additionalMethod(int[][] playingBoard) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (playingBoard[i][j] == 1) {
                    coordinates.add(new int[]{i, j});
                }
            }
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (playingBoard[i][j] == -1) {
                    checkingCells(i, j);
                }
            }
        }
    }
    private void changeChip(int number, int letter, int[][] playingBoard) {
        int sum = 0;
        boolean flag;
        for (int[] coordinate : coordinates) {
            flag = false;
            if (coordinate[0] - number == 0) {
                for (int i = Math.min(letter, coordinate[1]) + 1; i < Math.max(letter, coordinate[1]); ++i) {
                    if (playingBoard[number][i] == 0 || playingBoard[number][i] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (int i = Math.min(letter, coordinate[1]) + 1; i < Math.max(letter, coordinate[1]); ++i) {
                        playingBoard[number][i] = playingBoard[coordinate[0]][coordinate[1]];
                    }
                    playingBoard[number][letter] = playingBoard[coordinate[0]][coordinate[1]];
                    sum += Math.abs(letter - coordinate[1]) - 1;
                }
                continue;
            }
            if (coordinate[1] - letter == 0) {
                for (int i = Math.min(number, coordinate[0]) + 1; i < Math.max(number, coordinate[0]); ++i) {
                    if (playingBoard[i][letter] == 0 || playingBoard[i][letter] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (int i = Math.min(number, coordinate[0]) + 1; i < Math.max(number, coordinate[0]); ++i) {
                        playingBoard[i][letter] = playingBoard[coordinate[0]][coordinate[1]];
                    }
                    playingBoard[number][letter] = playingBoard[coordinate[0]][coordinate[1]];
                    sum += Math.abs(number - coordinate[0]) - 1;
                }
            }
            if (Math.abs(coordinate[0] - number) == Math.abs(coordinate[1] - letter)) {
                int i = number;
                int j = letter;
                i += number < coordinate[0] ? 1 : -1;
                j += letter < coordinate[1] ? 1 : -1;
                while (i != coordinate[0]) {
                    if (playingBoard[i][j] == 0 || playingBoard[i][j] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                    i += number < coordinate[0] ? 1 : -1;
                    j += letter < coordinate[1] ? 1 : -1;
                }
                if (!flag) {
                    i = number;
                    j = letter;
                    while (i != coordinate[0]) {
                        playingBoard[i][j] = playingBoard[coordinate[0]][coordinate[1]];
                        i += number < coordinate[0] ? 1 : -1;
                        j += letter < coordinate[1] ? 1 : -1;
                    }
                    sum += Math.abs(number - coordinate[0]) - 1;
                }
            }
        }
        if (playingBoard[number][letter] == -1) {
            Game.blackCount += sum + 1;
            Game.whiteCount -= sum;
        } else {
            Game.whiteCount += sum + 1;
            Game.blackCount -= sum;
        }
    }
    // Проверка на возможность повтора клетки в массиве возможных для игры клеток
    private void checkForUniqueness(int i, int j) {
        for (int[] element : possibleCoordinates) {
            if (element[0] == i && element[1] == j) {
                return;
            }
        }
        possibleCoordinates.add(new int[]{i, j});
    }
    // Проверка клеток
    private void checkingCells(int i, int j) {
        if (Game.checkCell(i - 1, j) && Game.chipsCount(i - 1, j, coordinates) >= 1) {
            checkForUniqueness(i - 1, j);
        }
        if (Game.checkCell(i - 1, j - 1) && Game.chipsCount(i - 1, j - 1, coordinates) >= 1) {
            checkForUniqueness(i - 1, j - 1);
        }
        if (Game.checkCell(i - 1, j + 1) && Game.chipsCount(i - 1, j + 1, coordinates) >= 1) {
            checkForUniqueness(i - 1, j + 1);
        }
        if (Game.checkCell(i, j - 1) && Game.chipsCount(i, j - 1, coordinates) >= 1) {
            checkForUniqueness(i, j - 1);
        }
        if (Game.checkCell(i, j + 1) && Game.chipsCount(i, j + 1, coordinates) >= 1) {
            checkForUniqueness(i, j + 1);
        }
        if (Game.checkCell(i + 1, j) && Game.chipsCount(i + 1, j, coordinates) >= 1) {
            checkForUniqueness(i + 1, j);
        }
        if (Game.checkCell(i + 1, j - 1) && Game.chipsCount(i + 1, j - 1, coordinates) >= 1) {
            checkForUniqueness(i + 1, j - 1);
        }
        if (Game.checkCell(i + 1, j + 1) && Game.chipsCount(i + 1, j + 1, coordinates) >= 1) {
            checkForUniqueness(i + 1, j + 1);
        }
    }
}
/* Класс для основных методов пользовательского интерфейса и генерация дальнейших дествий*/
class Menu {
    private static final Scanner in = new Scanner(System.in);
    // Приветствие пользователя
    private static void greetings() {
        System.out.println("Добро пожаловать!");
        System.out.println("Выберите пункт меню, укажите только цифру, при неверном значении программа попросит повторный ввод:");
        mainMenu();
    }
    // Главное меню пользователя
    private static void mainMenu() {
        System.out.println("1)Игра с компьютером");
        System.out.println("2)Игра с человеком");
        System.out.println("3)Лучшая статистика");
        System.out.println("4)Выход");
        int value = parseInt(1, 4);
        if (value == 1) {
            GameWithComputer game = new GameWithComputer();
            game.start();
        }
        if (value == 2) {
            GameWithPlayer game = new GameWithPlayer();
            game.start();
        }
        if(value == 3){
            System.out.println("Максимальное количество очков равняется: " + Game.maxRecord);
            System.out.println("Невероятный результат!");
        }
        if(value == 4){
            return;
        }
        System.out.println();
        mainMenu();
    }
    // Глубокое копирование двумерного массива
    protected static int[][] copy(int[][] src) {
        if (src == null) {
            return null;
        }

        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }

        return copy;
    }
    //Метод для обработки корректного ввода типа int пользователем
    public static int parseInt(int left, int right) {
        int value;
        do {
            System.out.println("Введите положительное число от " + left + " до " + right + ":");
            while (!in.hasNextInt()) {
                System.out.println("Некорректный ввод, попробуйте еще раз:");
                in.next();
            }
            value = in.nextInt();
        } while (value < left || value > right);
        return value;
    }

    public static void main(String[] args) {
        greetings();
    }
}
/* Унаследованный от Game класс для игры пользователя и компьютера*/
class GameWithComputer extends Game {
    @Override
    protected void start() {
        Player first = new Player(true);
        Player second = new Player(false);
        printTable();
        while (!isGameEnd() && first.makeMove(playingBoard) && second.easyMode(playingBoard)) {
            Game.stepCounter+=2;
            returnToStep();
        }
        if (blackCount > whiteCount) {
            System.out.println("Вы выиграли со счетом:");
            maxRecord = Math.max(blackCount,maxRecord);
            System.out.println("Black:" + blackCount);
        }
        if (blackCount < whiteCount) {
            System.out.println("Вы програли со счетом:");
            System.out.println("White:" + whiteCount);
        }
        if (blackCount == whiteCount) {
            System.out.println("Ничья!");
        }
    }
}
/* Унаследованный от Game класс для игры двух пользователей*/
class GameWithPlayer extends Game {
    @Override
    protected void start() {
        Player first = new Player(true);
        Player second = new Player(false);
        printTable();
        while (!isGameEnd() && first.makeMove(playingBoard) && second.makeMove(playingBoard)) {
            Game.stepCounter+=2;
            returnToStep();
        }
        if (blackCount > whiteCount) {
            System.out.println("Выиграл первый игрок с черными фишками");
            maxRecord = Math.max(blackCount, maxRecord);
        }
        if (blackCount < whiteCount) {
            System.out.println("Выиграл второй игрок с белыми фишками");
            maxRecord = Math.max(whiteCount,maxRecord);
        }
        if (blackCount == whiteCount) {
            System.out.println("Ничья!");
        }
    }
}
/* Абстрактный класс Game, в котором находятся основные методы и поля для дальнейшего наследования*/
abstract class Game {
    static protected int[][] playingBoard;
    static protected int stepCounter;
    static protected int maxRecord = 0;
    static protected Vector<int[][]> lastBoards;
    static protected int blackCount;
    static protected int whiteCount;

    protected Game() {
        stepCounter = 1;
        playingBoard = new int[8][8];
        playingBoard[3][3] = 1;
        playingBoard[4][4] = 1;
        playingBoard[3][4] = -1;
        playingBoard[4][3] = -1;
        blackCount = 2;
        whiteCount = 2;
        lastBoards = new Vector<>();
        lastBoards.add(Menu.copy(playingBoard));
    }

    static protected void printTable() {
        for (int i = 0; i < 8; ++i) {
            System.out.print(i + 1);
            System.out.print("|\t");
            for (int j = 0; j < 8; ++j) {
                if (playingBoard[i][j] == 0) {
                    System.out.print("O");
                    System.out.print("\t");
                    continue;
                }
                if (playingBoard[i][j] == 1) {
                    System.out.print("W");
                    System.out.print("\t");
                    continue;
                }
                if (playingBoard[i][j] == -1) {
                    System.out.print("B");
                    System.out.print("\t");
                }
            }
            if (i == 3) {
                System.out.print("\tBlack:");
                System.out.print(blackCount);

            }
            if (i == 4) {
                System.out.print("\tWhite:");
                System.out.print(whiteCount);
            }
            System.out.println();
        }
        System.out.print("\t");
        for (int i = 0; i < 8; ++i) {
            System.out.print("⎯\t");
        }
        System.out.print("\n\t");
        for (int i = 0; i < 8; ++i) {
            System.out.print(i + 1);
            System.out.print("\t");
        }
        System.out.println();
    }

    protected boolean isGameEnd() {
        if (blackCount == 64 || whiteCount == 64 || blackCount + whiteCount == 64) {
            return true;
        } else {
            return false;
        }
    }

    static protected int chipsCount(int number, int letter, ArrayList<int[]> coordinates) {
        int sum = 0;
        boolean flag;
        for (int[] coordinate : coordinates) {
            flag = false;
            if (coordinate[0] - number == 0) {
                for (int i = Math.min(letter, coordinate[1]) + 1; i < Math.max(letter, coordinate[1]); ++i) {
                    if (playingBoard[number][i] == 0 || playingBoard[number][i] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    sum += Math.abs(letter - coordinate[1]) - 1;
                }
                continue;
            }
            if (coordinate[1] - letter == 0) {
                for (int i = Math.min(number, coordinate[0]) + 1; i < Math.max(number, coordinate[0]); ++i) {
                    if (playingBoard[i][letter] == 0 || playingBoard[i][letter] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    sum += Math.abs(number - coordinate[0]) - 1;
                }
            }
            if (Math.abs(coordinate[0] - number) == Math.abs(coordinate[1] - letter)) {
                int i = number;
                int j = letter;
                i += number < coordinate[0] ? 1 : -1;
                j += letter < coordinate[1] ? 1 : -1;
                while (i != coordinate[0]) {
                    if (playingBoard[i][j] == 0 || playingBoard[i][j] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                    i += number < coordinate[0] ? 1 : -1;
                    j += letter < coordinate[1] ? 1 : -1;
                }
                if (!flag) {
                    sum += Math.abs(number - coordinate[0]) - 1;
                }
            }
        }
        return sum;
    }
    protected static void returnToStep(){
        System.out.println("Хотите ли вы вернуться к предыдущему ходу?");
        System.out.println("1 - да, 2 - нет");
        int value = Menu.parseInt(1,2);
        if(value == 1){
            System.out.println("Сейчас идет " + stepCounter + " ход. Выберите ход от " + 1 + " до " + (stepCounter-1) + ". Ход должен быть нечетным.");
            value = Menu.parseInt(1,stepCounter-1);
            while(value%2==0){
                value = Menu.parseInt(1,stepCounter-1);
            }
            playingBoard = lastBoards.get(value-1);
            blackCount = RecountBlackChip();
            whiteCount = RecountWhiteChip();
            Vector<int[][]> template = new Vector<>();
            for(int i = 0; i < value;++i){
                template.add(Menu.copy(lastBoards.get(i)));
            }
            lastBoards = template;
            stepCounter = value;
            printTable();
        }
        else{
            return;
        }
    }
    protected static int RecountBlackChip(){
        int sum = 0;
        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 8;++j){
                sum += playingBoard[i][j]==-1?1:0;
            }
        }
        return sum;
    }
    protected static int RecountWhiteChip(){
        int sum = 0;
        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 8;++j){
                sum += playingBoard[i][j]==1?1:0;
            }
        }
        return sum;
    }
    static protected float chipsFunction(int number, int letter, ArrayList<int[]> coordinates) {
        float sum = 0;
        boolean flag;
        for (int[] coordinate : coordinates) {
            flag = false;
            if (coordinate[0] - number == 0) {
                for (int i = Math.min(letter, coordinate[1]) + 1; i < Math.max(letter, coordinate[1]); ++i) {
                    if (playingBoard[number][i] == 0 || playingBoard[number][i] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (int i = Math.min(letter, coordinate[1]) + 1; i < Math.max(letter, coordinate[1]); ++i) {
                        sum+= countFuction(number,i);
                    }
                }
                continue;
            }
            if (coordinate[1] - letter == 0) {
                for (int i = Math.min(number, coordinate[0]) + 1; i < Math.max(number, coordinate[0]); ++i) {
                    if (playingBoard[i][letter] == 0 || playingBoard[i][letter] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (int i = Math.min(number, coordinate[0]) + 1; i < Math.max(number, coordinate[0]); ++i) {
                        sum += countFuction(i,letter);
                    }
                }
            }
            if (Math.abs(coordinate[0] - number) == Math.abs(coordinate[1] - letter)) {
                int i = number;
                int j = letter;
                i += number < coordinate[0] ? 1 : -1;
                j += letter < coordinate[1] ? 1 : -1;
                while (i != coordinate[0]) {
                    if (playingBoard[i][j] == 0 || playingBoard[i][j] == playingBoard[coordinate[0]][coordinate[1]]) {
                        flag = true;
                        break;
                    }
                    i += number < coordinate[0] ? 1 : -1;
                    j += letter < coordinate[1] ? 1 : -1;
                }
                if (!flag) {
                    i = number;
                    j = letter;
                    i += number < coordinate[0] ? 1 : -1;
                    j += letter < coordinate[1] ? 1 : -1;
                    while (i != coordinate[0]) {
                        sum += countFuction(i,j);
                        i += number < coordinate[0] ? 1 : -1;
                        j += letter < coordinate[1] ? 1 : -1;
                    }
                }
            }
        }
        if((number==0 && letter==0) || (number==0 && letter==7) || (number==7 && letter==0) || (number==7 && letter==7)){
            sum+=0.8;
        }
        if(number==0 || number == 7 || letter == 0 || letter == 7){
            sum+=0.4;
        }
        return sum;
    }

    static protected double countFuction(int number, int letter) {
        if (number == 0 || number == 7 || letter == 0 || letter == 7) {
            return 2;
        } else {
            return 1;
        }
    }

    static protected boolean checkCell(int number, int letter) {
        if (number < 0 || number > 7 || letter < 0 || letter > 7) {
            return false;
        } else {
            if (playingBoard[number][letter] == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    protected abstract void start();
}
