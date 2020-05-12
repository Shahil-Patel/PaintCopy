import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
public class PaintProgram extends JPanel implements MouseMotionListener, ActionListener, MouseListener,AdjustmentListener{
	ArrayList<Point> points;
	ArrayList<ArrayList<Point>> lines;
	JFrame frame;
	JMenuBar bar;
	JMenu menu;
	JScrollBar sb;
	JButton[] colorButtons;
	Color[] colors;
	Color colorSelected;
	int strokeSize;
	public PaintProgram(){
		points=new ArrayList<Point>();
		lines=new ArrayList<ArrayList<Point>>();
		frame=new JFrame("Paint Program");
		frame.add(this);
		bar=new JMenuBar();
		sb=new JScrollBar(JScrollBar.HORIZONTAL,1,0,1,50);
		sb.addAdjustmentListener(this);
		menu=new JMenu("Color List");
		colors=new Color[]{Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,Color.PINK,Color.BLACK,Color.CYAN};
		colorButtons=new JButton[colors.length];
		colorSelected=colors[3];
		menu.setLayout(new GridLayout(1,7));
		for(int x=0;x<colors.length;x++){
			colorButtons[x]=new JButton();
			colorButtons[x].addActionListener(this);
			colorButtons[x].putClientProperty("colorNum",x);
			colorButtons[x].setBackground(colors[x]);
			menu.add(colorButtons[x]);
		}
		bar.add(menu);
		bar.add(sb);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		frame.add(bar,BorderLayout.NORTH);
		frame.setSize(1000,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		g.setColor(Color.WHITE);
		g.fillRect(0,0,frame.getWidth(),frame.getHeight());
		for(int x=0;x<points.size()-1;x++){
			g.setColor(points.get(x).getColor());
			g2.setStroke(new BasicStroke(points.get(x).getStrokeSize()));
			g.drawLine(points.get(x).getX(),points.get(x).getY(),points.get(x+1).getX(),points.get(x+1).getY());
		}
		for(int x=0;x<lines.size();x++){
			for(int y=0;y<lines.get(x).size()-1;y++){
				g.setColor(lines.get(x).get(y).getColor());
				g2.setStroke(new BasicStroke(lines.get(x).get(y).getStrokeSize()));
				g.drawLine(lines.get(x).get(y).getX(),lines.get(x).get(y).getY(),lines.get(x).get(y+1).getX(),lines.get(x).get(y+1).getY());

			}
		}
	}
	public void actionPerformed(ActionEvent e){
		colorSelected=colors[Integer.parseInt(((JButton)e.getSource()).getClientProperty("colorNum")+"")];
		repaint();
	}
	public void mouseDragged(MouseEvent e){
		points.add(new Point(e.getX(),e.getY(),colorSelected,Math.round(strokeSize)));
		colors[7]=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
		colorButtons[7].setBackground(colors[7]);
		repaint();
	}
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if(e.getSource()==sb)
		{
			strokeSize=sb.getValue();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e){
		lines.add(points);
		points=new ArrayList<Point>();
		repaint();
	}
	public void mouseMoved(MouseEvent e){

	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	public static void main(String[] args)
	{
		PaintProgram app=new PaintProgram();
	}
	public class Point{
		private int x,y,strokeSize;
		private Color color;
		public Point(int x, int y, Color color,int strokeSize){
			this.x=x;
			this.y=y;
			this.color=color;
			this.strokeSize=strokeSize;
		}
		public int getStrokeSize(){
			return strokeSize;
		}
		public int getX(){
			return x;
		}
		public int getY(){
			return y;
		}
		public Color getColor(){
			return color;
		}
	}
}