package cc.wulian.smarthomev6.support.core.socket;

import android.support.annotation.NonNull;
import cc.wulian.smarthomev6.support.utils.WLog;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import cc.wulian.smarthomev6.support.core.socket.receiver.ISocketReceiver;

/**
 * Created by zbl on 2017/3/6.
 * Socket通信模块
 */

public class SocketClient {

    private static final String TAG = "SocketClient";

    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ReaderThread readerThread;
    private WriterThread writerThread;
    private boolean isAlive = false;

    private List<ISocketReceiver> receivers = Collections.synchronizedList(new ArrayList<ISocketReceiver>());

    private SocketClientListener socketClientListener;

    public interface SocketClientListener {
        void onConnectSuccess();

        void onReceive(String text);
    }

    public SocketClient(@NonNull SocketClientListener listener) {
        this.socketClientListener = listener;
    }

    public void send(String text) {
        queue.offer(text);
    }

    public void connect(final String host, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isAlive) {
                    disconnect();
                }
                try {
                    socket = new Socket(host, port);
                    socket.setKeepAlive(true);
                    reader = new BufferedReader((new InputStreamReader(socket.getInputStream())));
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    isAlive = true;
                    readerThread = new ReaderThread();
                    readerThread.start();
                    writerThread = new WriterThread();
                    writerThread.start();
                    socketClientListener.onConnectSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                while (isAlive){
//                    send("{\"cmd\":\"00\"}");
//                    try {
//                        sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }).start();
    }

    public void disconnect() {
        WLog.w(TAG,"关闭socketClient");
        isAlive = false;
        queue.clear();
        receivers.clear();
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                writer = null;
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader = null;
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
            readerThread = null;
        }
        if (writerThread != null && writerThread.isAlive()) {
            writerThread.interrupt();
            writerThread = null;
        }
    }

    public void registReceiver(ISocketReceiver receiver) {
        if (!receivers.contains(receiver)) {
            receivers.add(receiver);
        }
    }

    public void unregistReceiver(ISocketReceiver receiver) {
        receivers.remove(receiver);
    }

    class ReaderThread extends Thread {

        @Override
        public void run() {
            while (isAlive) {
                try {
                    String text = reader.readLine();
                    if (socketClientListener != null) {
                        if (text != null) {
                            socketClientListener.onReceive(text);
                            for (ISocketReceiver receiver : receivers) {
                                receiver.onReceive(SocketClient.this, text);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                char[] buffer = new char[2048];
//                try {
//                    int len = reader.read(buffer);
//                    if (len > 0) {
//                        if (socketClientListener != null) {
//                            socketClientListener.onReceive(new String(buffer).trim());
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }


    class WriterThread extends Thread {

        @Override
        public void run() {
            while (isAlive) {
                try {
                    String text = queue.take();
                    writer.write(text);
                    //结束符号
                    writer.write(0x0d);
                    writer.write(0x0a);
                    writer.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
