import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class Swing_UI {
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            // JFrame 생성
	            JFrame frame = new JFrame("Swing 예제");
	            frame.setSize(300, 200);
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	            // JLabel 추가
	            JLabel label = new JLabel("안녕하세요, Swing!", SwingConstants.CENTER);
	            frame.add(label);

	            // 창 표시
	            frame.setVisible(true);
	        });
	    }
}
