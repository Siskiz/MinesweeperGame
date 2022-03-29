package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 15;
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;
    private boolean isGameOver;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(0);
        showMessageDialog(Color.RED, "Again?", Color.BLACK, 50);
        isGameOver = true;
        createGame();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j].isMine) {
                    continue;
                }
                List<GameObject> list = getNeighbors(gameField[i][j]);
                for (GameObject a : list) {
                    if (a.isMine) {
                        gameField[i][j].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (!gameField[x][y].isOpen && !gameField[y][x].isMine) {
            gameField[x][y].isOpen = true;
            setCellColor(x, y, Color.GREEN);
            countClosedTiles--;
            score += 5;
        }
        setScore(score);
        if (isGameStopped) {
            return;
        }
        if (gameField[x][y].isFlag) {
            return;
        }
        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        } else if (gameField[y][x].countMineNeighbors == 0) {
            List<GameObject> neighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
            for (GameObject a : neighbors) {
                if (gameField[a.x][a.y].isOpen) {
                    continue;
                }
                setCellValue(a.x, a.y, "");
                openTile(a.x, a.y);
            }
        } else {
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
        }
        if (countClosedTiles == countMinesOnField) {
            win();
        }
    }

    private void markTile(int x, int y) {
        if (isGameStopped) {
            return;
        }
        if (gameField[x][y].isOpen) {
            return;
        }
        if (countFlags == 0 && !gameField[x][y].isFlag) {
            return;
        }
        if (!gameField[x][y].isFlag) {
            gameField[x][y].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else {
            gameField[x][y].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game Over", Color.WHITE, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You Win", Color.WHITE, 50);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameOver) {
            isGameOver = false;
            return;
        }
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

}