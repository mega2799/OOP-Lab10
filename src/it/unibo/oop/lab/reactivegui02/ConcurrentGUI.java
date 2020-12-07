package it.unibo.oop.lab.reactivegui02;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

public class ConcurrentGUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -5809059974380444922L;
    private static int X_DIMENSION = 600; // wanted to try prefixed dimension
    private static int Y_DIMENSION = 150;

    private JPanel panel = new JPanel(new FlowLayout());
    private JLabel label = new JLabel();
    private final JButton upButton = new JButton("UP");
    private final JButton downButton = new JButton("DOWN");
    private final JButton stopButton = new JButton("STOP");

    public ConcurrentGUI() {
        super("Concurrent GUI");
        this.setSize(X_DIMENSION, Y_DIMENSION);
        panel.add(label);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(stopButton);
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        upButton.addActionListener(e -> agent.swtichOperation(true));
        downButton.addActionListener(e -> agent.swtichOperation(false));
        stopButton.addActionListener(e -> {
            agent.stopRunning();
            downButton.setEnabled(false);
            upButton.setEnabled(false);
            stopButton.setEnabled(false);
        });
    }

    public class Agent implements Runnable {

        private volatile int runCount;
        private volatile boolean isAdding;
        private volatile boolean isRunning;

        public Agent() {
            super();
            this.runCount = 0;
            this.isAdding = true;
            this.isRunning = true;
        }

        public final void swtichOperation(final boolean operation) {
            this.isAdding = operation;
        }

        public final String applyOperation(final int cnt) {
            if (this.isAdding) {
                return String.valueOf(this.runCount++);
            }
            return String.valueOf(this.runCount--);
        }

        public final void stopRunning() {
            this.isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(100);
                    SwingUtilities.invokeLater(() -> ConcurrentGUI.this.label.setText(this.applyOperation(runCount)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
