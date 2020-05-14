import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
public class PaintProgram extends JPanel implements MouseMotionListener, ActionListener, MouseListener,AdjustmentListener{
	ArrayList<Point> points;
	ArrayList<ArrayList<Point>> lines;
	ArrayList<Shape> shapes;
	JFrame frame;
	JMenuBar bar;
	JMenu menu;
	JScrollBar sb;
	JMenuItem[] colorButtons;
	JButton freeButton,rectButton;
	boolean freeButtonOn,rectButtonOn,first;
	int currX,currY,currWidth,currHeight;
	Color[] colors;
	Color colorSelected;
	Shape currShape;
	int strokeSize;
	public PaintProgram(){
		points=new ArrayList<Point>();
		lines=new ArrayList<ArrayList<Point>>();
		shapes=new ArrayList<Shape>();
		freeButtonOn=true;
		rectButtonOn=false;
		first=true;
		frame=new JFrame("Paint Program");
		frame.add(this);
		bar=new JMenuBar();
		sb=new JScrollBar(JScrollBar.HORIZONTAL,5,0,1,50);
		strokeSize=5;
		sb.addAdjustmentListener(this);
		menu=new JMenu("Color List");
		colors=new Color[]{Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,Color.PINK,Color.BLACK,Color.CYAN};
		colorButtons=new JMenuItem[colors.length];
		freeButton=new JButton("Free Draw");
		rectButton=new JButton("Rectangle");
		freeButton.addActionListener(this);
		rectButton.addActionListener(this);
		colorSelected=colors[3];
		menu.setLayout(new GridLayout(1,7));
		for(int x=0;x<colors.length;x++){
			colorButtons[x]=new JMenuItem();
			colorButtons[x].setPreferredSize(new Dimension(40,20));
			colorButtons[x].addActionListener(this);
			colorButtons[x].setBackground(colors[x]);
			menu.add(colorButtons[x]);
		}
		bar.add(menu);
		bar.add(freeButton);
		bar.add(rectButton);
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
		for(int x=0;x<shapes.size();x++){
			g.setColor(shapes.get(x).getColor());
			g2.setStroke(new BasicStroke(shapes.get(x).getStrokeSize()));
			if(shapes.get(x) instanceof Block){
				g2.draw(((Block)shapes.get(x)).getRect());
			}
		}
		if(rectButtonOn&&currShape!=null){
			g.setColor(colorSelected);
			g2.setStroke(new BasicStroke(currShape.getStrokeSize()));
			g2.draw(((Block)currShape).getRect());
		}
	}
	public void actionPerformed(ActionEvent e){
		for(int x=0;x<colors.length;x++){
			String[] str=e.getSource().toString().split(",");
			int num=Integer.parseInt(str[2]);
			if(num>0){
				if((num-3)/20==x){
					colorSelected=colors[x];
				}
			}
		}
		if(e.getSource()==freeButton){
			freeButtonOn=true;
			rectButtonOn=false;
		}
		if(e.getSource()==rectButton){
			freeButtonOn=false;
			rectButtonOn=true;
		}
		repaint();
	}
	public void mouseDragged(MouseEvent e){
		if(freeButtonOn){
			points.add(new Point(e.getX(),e.getY(),colorSelected,Math.round(strokeSize)));
		}
		if(rectButtonOn){
			if(first){
				currX=e.getX();
				currY=e.getY();
				currShape=new Block(currX,currY,0,0,Math.round(strokeSize),colorSelected);
				first=false;
			}
			else{
				if(currX<e.getX()&&currY<e.getY()){
					currWidth=Math.abs(e.getX()-currX);
					currHeight=Math.abs(e.getY()-currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				}
				else if(currX>e.getX()&&currY<e.getY()){
					currShape.setX(e.getX());
					currWidth=Math.abs(e.getX()-currX);
					currHeight=Math.abs(e.getY()-currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				}
				else if(currX<e.getX()&&currY>e.getY()){
					currShape.setY(e.getY());
					currWidth=Math.abs(e.getX()-currX);
					currHeight=Math.abs(e.getY()-currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				}
				else if(currX>e.getX()&&currY>e.getY()){
					currShape.setX(e.getX());
					currShape.setY(e.getY());
					currWidth=Math.abs(e.getX()-currX);
					currHeight=Math.abs(e.getY()-currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				}
			}
		}
		colors[7]=new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
		colorButtons[7].setBackground(colors[7]);
		repaint();
	}
	public void adjustmentValueChanged(AdjustmentEvent e){
		if(e.getSource()==sb){
			strokeSize=sb.getValue();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e){
		if(freeButtonOn){
			lines.add(points);
			points=new ArrayList<Point>();
		}
		if(rectButtonOn){
			first=true;
			shapes.add(currShape);
		}
			repaint();
	}
	public void mouseMoved(MouseEvent e){
		// TODO Auto-generated method stub
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
	public static void main(String[] args){
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
	public class Shape{
		private int x,y,width,height,strokeSize;
		private Color color;
		public Shape(int x, int y, int width, int height, int strokeSize, Color color) {
			this.x= x;
			this.y= y;
			this.width= width;
			this.height= height;
			this.strokeSize= strokeSize;
			this.color =color;
		}
		public int getX(){
			return x;
		}
		public int getY(){
			return y;
		}
		public int getWidth(){
			return width;
		}
		public int getHeight(){
			return height;
		}
		public void setHeight(int height){
			this.height=height;
		}
		public void setWidth(int width){
			this.width=width;
		}
		public void setX(int x){
			this.x=x;
		}
		public void setY(int y){
			this.y=y;
		}
		public int getStrokeSize(){
			return strokeSize;
		}
		public Color getColor(){
			return color;
		}
	}
	public class Block extends Shape{
		public Block(int x, int y, int width, int height, int strokeSize, Color color) {
			super(x,y,width,height,strokeSize,color);
		}
		public Rectangle getRect(){
			return new Rectangle(getX(),getY(),getWidth(),getHeight());
		}
	}
}