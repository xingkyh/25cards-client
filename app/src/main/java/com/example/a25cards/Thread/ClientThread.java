package com.example.a25cards.Thread;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.a25cards.model.Deck;
import com.example.a25cards.model.GameState;
import com.example.a25cards.model.Poker;
import com.example.a25cards.model.User;
import com.example.a25cards.ui.GetCallThread;
import com.example.a25cards.util.PokerTool;
import com.example.a25cards.util.Rule;
import com.example.a25cards.view.GameView;

public class ClientThread implements Runnable {
    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    Handler handler;
    // 定义接收UI线程的Handler对象
    public Handler revHandler;
    // 该线程处理Socket所对用的输入输出流
    DataInputStream is = null;
    DataOutputStream os = null;
    List<Poker> pokers = new ArrayList<Poker>();
    GameView gameView;
    private User[] others = new User[6];
    private int[] seatnum = new int[6];
    private ArrayList<User> users = new ArrayList<User>();
    public ClientThread(Handler handler, GameView gameView) {
        this.handler = handler;
        this.gameView = gameView;
    }

    public static boolean win = false;
    @Override
    public void run() {
        s = new Socket();
        try {
            s.connect(new InetSocketAddress("172.20.10.3", 3000), 5000);
            s.setKeepAlive(true);
            is = new DataInputStream(s.getInputStream());
            os = new DataOutputStream(s.getOutputStream());
            // 启动一条子线程来读取服务器相应的数据
            new Thread() {

                @Override
                public void run() {
                    String content = null;
                    // 不断的读取Socket输入流的内容
                    try {
                        while (true) {
                            content = is.readUTF();
                            if(content.startsWith("start")){
                                int myseat= Integer.parseInt(is.readUTF());
                                gameView.setMyseat(myseat);
                                for(int j = 0; j < 3; j++){
                                    seatnum[j] = Integer.parseInt(is.readUTF());
                                    if (seatnum[j] > myseat) {
                                        users.add(new User(gameView.user.getUsername(),"",""));
                                    }
                                    users.add(new User(is.readUTF(),"",""));
                                }
                                gameView.setUsers(users);
                                String po = is.readUTF();
                                String poker[] = po.split(",");

                                for(int j = 0; j < poker.length; j++){
                                    String temp[] = poker[j].split("aa");
                                    System.out.println(temp[0]+"   "+temp[1]);
                                    pokers.add(new Poker(Integer.parseInt(temp[1]),temp[0]));
                                }
                                gameView.getMyDeck().setPokersHand(pokers);
                                gameView.setState(GameState.DEAL_CARDS);
                            }else if(content.startsWith("call")){

                                gameView.setMyTurn(true);
                                gameView.setState(GameState.CALL_SCORE);

                            }else if(content.startsWith("boss")){
                                int boss = Integer.parseInt(is.readUTF());
                                gameView.setBoss(boss);
                                if(gameView.getMyseat() == boss){
                                    String po = is.readUTF();
                                    String poker[] = po.split(",");
                                    pokers = gameView.getMyDeck().getPokersHand();
                                    for(int j = 0; j < poker.length; j++){
                                        String temp[] = poker[j].split("aa");
                                        System.out.println(temp[0]+"   "+temp[1]);
                                        Poker pok = new Poker(Integer.parseInt(temp[1]),temp[0]);
                                        pok.setSelected(true);
                                        pokers.add(pok);
                                    }
                                    gameView.setState(GameState.GET_CARDS);
                                    new GetCallThread(gameView).start();
                                }else{
                                    gameView.setMyTurn(false);
                                    gameView.setState(GameState.MY_DISCARD);
                                }
                            }else if(content.equals("discard")){

                                String po = is.readUTF();
                                System.out.println("discard");
                                System.out.println("pokers: "+po);
                                String poker[] = po.split(",");
                                Deck _lastDeck = new Deck();
                                for(int j = 0; j < poker.length; j++){
                                    String temp[] = poker[j].split("aa");
                                    _lastDeck.getPokersHand().add(new Poker(Integer.parseInt(temp[1]), temp[0]));
                                    PokerTool.addToMap(_lastDeck.getCardsMap(), Integer.parseInt(temp[1]));
                                }
                                _lastDeck.setSumCards(_lastDeck.getPokersHand().size());
                                Rule.judgeType(_lastDeck);
                                gameView.setLastType(_lastDeck.getType());
                                gameView.setLastWeight(_lastDeck.getWeight());
                                gameView.setLastDeck(_lastDeck);
                                gameView.setMyTurn(true);
                            }else if(content.equals("newDiscard")){
                                System.out.println("newDiscard");

                                gameView.setLastDeck(new Deck());
                                gameView.setState(GameState.MY_DISCARD);
                                gameView.setLastType(0);
                                gameView.setLastWeight(0);
                                gameView.setMyTurn(true);
                            }else if(content.equals("winner")){
                                int winner = Integer.parseInt(is.readUTF());
                                if (gameView.getMyseat()==winner){
                                    win = true;
                                }
                                gameView.setState(GameState.GAME_END);
                            }else if(content.equals("lastDiscard")){
                                String po = is.readUTF();
                                if(po.equals("clear")){
                                    gameView.setLastDeck(new Deck());
                                    gameView.setLastType(0);
                                    gameView.setLastWeight(0);
                                }else{
                                    String poker[] = po.split(",");
                                    Deck _lastDeck = new Deck();
                                    for(int j = 0; j < poker.length; j++){
                                        String temp[] = poker[j].split("aa");
                                        _lastDeck.getPokersHand().add(new Poker(Integer.parseInt(temp[1]), temp[0]));
                                        PokerTool.addToMap(_lastDeck.getCardsMap(), Integer.parseInt(temp[1]));
                                    }
                                    _lastDeck.setSumCards(_lastDeck.getPokersHand().size());
                                    Rule.judgeType(_lastDeck);
                                    gameView.setLastType(_lastDeck.getType());
                                    gameView.setLastWeight(_lastDeck.getWeight());
                                    gameView.setLastDeck(_lastDeck);
                                }
                            }
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }

            }.start();
            // 为当前线程初始化Looper
            Looper.prepare();
            // 创建revHandler对象
            revHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x345) {

                        try {
                            os.write((msg.obj.toString() + "\r\n")
                                    .getBytes("gbk"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x789){
                        try {
                            os.writeUTF("enter");
                            os.writeUTF(gameView.getUser().getUsername());
                            os.writeUTF(gameView.getUser().getPwd());
                            os.writeUTF(gameView.getUser().getNickname());
                            os.writeUTF("ready");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x1234){
                        try {
                            os.writeUTF("call");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x5678){
                        try {
                            os.writeUTF("noCall");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x666){
                        try {
                            os.writeUTF("cardSendEnd");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x7777){
                        System.out.println("fuck");
                        String p = "";
                        for(int i = 0; i < gameView.getMyDeck().getPokersHand().size(); i++){
                            if(gameView.getMyDeck().getPokersHand().get(i).isSelected()){
                                p = p.concat(gameView.getMyDeck().getPokersHand().get(i).toString()+",");
                            }
                        }
                        System.out.println("0x7777 pokers:  "+p);
                        PokerTool.eraseCards(gameView.getMyDeck());
                        PokerTool.getNewPos(gameView);
                        PokerTool.gatherCards(gameView.getMyDeck());
                        PokerTool.resetMap(gameView.getMyDeck());
                        gameView.getMyDeck().setSumCards(0);
                        gameView.resetStatus();
                        try {
                            gameView.setMyTurn(false);
                            os.writeUTF("discard:");
                            os.writeUTF(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x8888){
                        gameView.setMyTurn(false);
                        try {
                            os.writeUTF("pass:");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msg.what == 0x9999){
                        try {
                            os.writeUTF("win");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            // 启动Looper
            Looper.loop();

        } catch (SocketTimeoutException e) {
            Message msg = new Message();
            msg.what = 0x123;
            msg.obj = "网络连接超时！";
            handler.sendMessage(msg);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}