package sweeper;

import sun.jvm.hotspot.debugger.Page;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Bomb bomb;
    private Flag flag;

    private GameState state;

    public Game(int cols, int rows, int bombs){
        Ranges.setSize(new Coord(cols,rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
    }

    public void start(){
        bomb.start();
        flag.start();
        state = GameState.PLAYED;
    }

    public Box getBox(Coord coord){
        if(flag.get(coord) == Box.OPENED)
        return bomb.get(coord);
        else return flag.get(coord);
    }

    public GameState getState() {
        return state;
    }

    public void pressLeftButton(Coord coord){
        if(isGameOver())
            return;
            openBox(coord);
            checkWinner();
            checkSurroundedBombs();
    }

    private void checkSurroundedBombs() {
        if(state == GameState.PLAYED)
            for (Coord coord : Ranges.getAllCoords()) {
                boolean isAroundEmpty = true;
                if (bomb.get(coord) == Box.BOMB) {
                    for (Coord around : Ranges.getCoordsAround(coord))
                        if(flag.get(around) == Box.CLOSED) { isAroundEmpty = false;break; }
                    if(isAroundEmpty && flag.get(coord) != Box.FLAGED)
                        flag.toggleFlagedToBox(coord);
                }
            }
    }

    public void pressRightButton(Coord coord){
        if(isGameOver())
            return;
        flag.toggleFlagedToBox(coord);
    }

    private boolean isGameOver(){
        if(state != GameState.PLAYED) {
            start();
            return true;
        }
        return false;
    }

    private void checkWinner() {
        if(state == GameState.PLAYED)
            if(flag.getTotalClosed() == bomb.getTotalBombs()){
                state = GameState.WINNER;
                flag.setFlagedToLastClosedBoxes();
            }
    }

    public int getTotalBombs(){
        return bomb.getTotalBombs();
    }

    public int getTotalFlags(){
        return flag.getTotalFlaged();
    }

    private void openBox(Coord coord) {
        switch (flag.get(coord)){
            case OPENED: setOpenedToClosedBoxesAroundNumber(coord);break;
            case FLAGED: break;
            case CLOSED:
                switch (bomb.get(coord)){
                    case ZERO: openBoxesAroundZero(coord); break;
                    case BOMB: openBombs(coord); break;
                    default: flag.setOpenedToBox(coord); break;
                }
        }
    }

    private void setOpenedToClosedBoxesAroundNumber(Coord coord) {
        if(Box.BOMB != bomb.get(coord))
            if(bomb.get(coord).getNumber() == flag.getCountOfFlagedBoxesAround(coord))
                for(Coord around : Ranges.getCoordsAround(coord)) {
                    //System.out.println(around.x + " " + around.y);
                    if (flag.get(around) == Box.CLOSED)
                        openBox(around);
                }
    }

    private void openBombs(Coord bombedCoord) {
        flag.setBombedToBox(bombedCoord);
        for(Coord coord : Ranges.getAllCoords())
            if(bomb.get(coord) == Box.BOMB){
                flag.setOpenedToClosedBox(coord);
            }else
                flag.setNoBombToFlagedBox(coord);
        state = GameState.BOMBED;
    }

    private void openBoxesAroundZero(Coord coord) {
        //System.out.println(coord.x + " " + coord.y);
        flag.setOpenedToBox(coord);
        for(Coord around : Ranges.getCoordsAround(coord))
            openBox(around);
    }

}
