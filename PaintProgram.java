import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.io.FileFilter;

import javax.imageio.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.*;

public class PaintProgram extends JPanel implements MouseMotionListener, ActionListener, MouseListener,
		AdjustmentListener, javax.swing.event.ChangeListener {
	ArrayList<Point> points;
	ArrayList<ArrayList<Point>> lines;
	ArrayList<Shape> shapes;
	JFrame frame;
	JMenuBar bar;
	JMenuItem save, open;
	JMenu file;
	JMenu menu;
	JColorChooser colorChooser;
	JScrollBar sb;
	JMenuItem[] colorButtons;
	JButton freeButton, rectButton, undoButton, redoButton;
	boolean freeButtonOn, rectButtonOn, first;
	int currX, currY, currWidth, currHeight;
	Color[] colors;
	Color colorSelected;
	Shape currShape;
	JFileChooser fileChooser;
	BufferedImage loadedImg;
	int strokeSize;
	ImageIcon undo, redo, line, rect;
	Stack<Shape> undoShapes;
	Stack<ArrayList<Point>> undoLines;
	Stack<String> cmdOrder;
	Stack<String> undoCmdOrder;

	public PaintProgram() {
		points = new ArrayList<Point>();
		lines = new ArrayList<ArrayList<Point>>();
		shapes = new ArrayList<Shape>();
		undoShapes = new Stack<Shape>();
		undoLines = new Stack<ArrayList<Point>>();
		cmdOrder = new Stack<String>();
		undoCmdOrder = new Stack<String>();
		undo = new ImageIcon("images/undo.png");
		redo = new ImageIcon("images/redo.png");
		line = new ImageIcon("images/lines.png");
		rect = new ImageIcon("images/rect.png");
		undo = new ImageIcon(undo.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		redo = new ImageIcon(redo.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		line = new ImageIcon(line.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		rect = new ImageIcon(rect.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		freeButtonOn = true;
		rectButtonOn = false;
		first = true;
		frame = new JFrame("Paint Program");
		frame.add(this);
		bar = new JMenuBar();
		sb = new JScrollBar(JScrollBar.HORIZONTAL, 5, 0, 1, 50);
		strokeSize = 5;
		sb.addAdjustmentListener(this);
		menu = new JMenu("Color List");
		colors = new Color[] { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PINK, Color.BLACK,
				Color.CYAN };
		colorButtons = new JMenuItem[colors.length];
		file = new JMenu("File");
		save = new JMenuItem("Save");
		open = new JMenuItem("Open");
		save.addActionListener(this);
		open.addActionListener(this);
		file.add(save);
		file.add(open);
		freeButton = new JButton();
		rectButton = new JButton();
		undoButton = new JButton();
		redoButton = new JButton();
		undoButton.addActionListener(this);
		redoButton.addActionListener(this);
		freeButton.addActionListener(this);
		rectButton.addActionListener(this);
		freeButton.setIcon(line);
		rectButton.setIcon(rect);
		undoButton.setIcon(undo);
		redoButton.setIcon(redo);
		undoButton.setFocusPainted(false);
		redoButton.setFocusPainted(false);
		freeButton.setFocusPainted(false);
		rectButton.setFocusPainted(false);
		colorSelected = colors[6];
		menu.setLayout(new GridLayout(1, 7));
		for (int x = 0; x < colors.length; x++) {
			colorButtons[x] = new JMenuItem();
			colorButtons[x].setPreferredSize(new Dimension(40, 20));
			colorButtons[x].addActionListener(this);
			colorButtons[x].setBackground(colors[x]);
			menu.add(colorButtons[x]);
		}
		colorChooser = new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(this);
		freeButton.setBackground(Color.lightGray);
		menu.add(colorChooser);
		bar.add(file);
		bar.add(menu);
		bar.add(freeButton);
		bar.add(rectButton);
		bar.add(undoButton);
		bar.add(redoButton);
		bar.add(sb);
		fileChooser = new JFileChooser(System.getProperty("user.dir"));
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		frame.add(bar, BorderLayout.NORTH);
		frame.setSize(1000, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.white);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		if(loadedImg!=null){
			g.drawImage(loadedImg, 0, 0, null);
		}
		for (int x = 0; x < points.size() - 1; x++) {
			g.setColor(points.get(x).getColor());
			g2.setStroke(new BasicStroke(points.get(x).getStrokeSize()));
			g.drawLine(points.get(x).getX(), points.get(x).getY(), points.get(x + 1).getX(), points.get(x + 1).getY());
		}
		for (int x = 0; x < lines.size(); x++) {
			for (int y = 0; y < lines.get(x).size() - 1; y++) {
				g.setColor(lines.get(x).get(y).getColor());
				g2.setStroke(new BasicStroke(lines.get(x).get(y).getStrokeSize()));
				g.drawLine(lines.get(x).get(y).getX(), lines.get(x).get(y).getY(), lines.get(x).get(y + 1).getX(),
						lines.get(x).get(y + 1).getY());
			}
		}
		for (int x = 0; x < shapes.size(); x++) {
			g.setColor(shapes.get(x).getColor());
			g2.setStroke(new BasicStroke(shapes.get(x).getStrokeSize()));
			if (shapes.get(x) instanceof Block) {
				g2.draw(((Block) shapes.get(x)).getRect());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		for (int x = 0; x < colors.length; x++) {
			String[] str = e.getSource().toString().split(",");
			int num = Integer.parseInt(str[2]);
			if (num > 0) {
				if ((num - 3) / 20 == x) {
					colorSelected = colors[x];
				}
			}
		}
		if (e.getSource() == freeButton) {
			freeButtonOn = true;
			rectButtonOn = false;
			freeButton.setBackground(Color.lightGray);
			rectButton.setBackground(null);
		}
		if (e.getSource() == rectButton) {
			freeButtonOn = false;
			rectButtonOn = true;
			rectButton.setBackground(Color.lightGray);
			freeButton.setBackground(null);
		}
		if (e.getSource() == undoButton) {
			if (cmdOrder.size() > 0) {
				String temp = cmdOrder.pop();
				undoCmdOrder.push(temp);
				if (temp.equals("line")) {
					if (lines.size() > 0) {
						undoLines.push(lines.remove(lines.size() - 1));
						repaint();
					}
				}
				if (temp.equals("shape")) {
					if (shapes.size() > 0) {
						undoShapes.push(shapes.remove(shapes.size() - 1));
						repaint();
					}
				}
			}
		}
		if (e.getSource() == redoButton) {
			if (undoCmdOrder.size() > 0) {
				String temp = undoCmdOrder.pop();
				cmdOrder.push(temp);
				if (temp.equals("line")) {
					if (undoLines.size() > 0) {
						lines.add(undoLines.pop());
						repaint();
					}
				}
				if (temp.equals("shape")) {
					if (undoShapes.size() > 0) {
						shapes.add(undoShapes.pop());
						repaint();
					}
				}
			}
		}
		if (e.getSource() == save) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.png", "png");
			fileChooser.setFileFilter(filter);
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try {
					String st = file.getAbsolutePath();
					if (st.indexOf(".png") >= 0) {
						st = st.substring(0, st.length() - 4);
					}
					ImageIO.write(createImage(), "png", new File(st + ".png"));
				} catch (Exception a) {
					// TODO: handle exception
				}
			}
		}
		if (e.getSource() == open) {
			fileChooser.showOpenDialog(null);
			File imgFile = fileChooser.getSelectedFile();
			try {
				loadedImg = ImageIO.read(imgFile);
			} catch (Exception a) {
				// TODO: handle exception
			}
			points = new ArrayList<Point>();
			lines = new ArrayList<ArrayList<Point>>();
			shapes = new ArrayList<Shape>();
			undoShapes = new Stack<Shape>();
			undoLines = new Stack<ArrayList<Point>>();
			cmdOrder = new Stack<String>();
			undoCmdOrder = new Stack<String>();
			repaint();
		}
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		if (freeButtonOn) {
			points.add(new Point(e.getX(), e.getY(), colorSelected, Math.round(strokeSize)));
		}
		if (rectButtonOn) {
			if (first) {
				currX = e.getX();
				currY = e.getY();
				currShape = new Block(currX, currY, 0, 0, Math.round(strokeSize), colorSelected);
				first = false;
				shapes.add(currShape);
			} else {
				if (currX < e.getX() && currY < e.getY()) {
					currWidth = Math.abs(e.getX() - currX);
					currHeight = Math.abs(e.getY() - currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				} else if (currX > e.getX() && currY < e.getY()) {
					currShape.setX(e.getX());
					currWidth = Math.abs(e.getX() - currX);
					currHeight = Math.abs(e.getY() - currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				} else if (currX < e.getX() && currY > e.getY()) {
					currShape.setY(e.getY());
					currWidth = Math.abs(e.getX() - currX);
					currHeight = Math.abs(e.getY() - currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				} else if (currX > e.getX() && currY > e.getY()) {
					currShape.setX(e.getX());
					currShape.setY(e.getY());
					currWidth = Math.abs(e.getX() - currX);
					currHeight = Math.abs(e.getY() - currY);
					currShape.setHeight(currHeight);
					currShape.setWidth(currWidth);
				}
			}
		}
		colors[7] = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
		colorButtons[7].setBackground(colors[7]);
		repaint();
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == sb) {
			strokeSize = sb.getValue();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (freeButtonOn) {
			lines.add(points);
			points = new ArrayList<Point>();
			cmdOrder.push("line");
		}
		if (rectButtonOn) {
			cmdOrder.push("shape");
		}
		repaint();
	}

	public BufferedImage createImage() {
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		this.paint(g2);
		g2.dispose();
		return img;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		first = true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		colorSelected = colorChooser.getColor();

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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

	public static void main(String[] args) {
		PaintProgram app = new PaintProgram();
	}

	public class Point {
		private int x, y, strokeSize;
		private Color color;

		public Point(int x, int y, Color color, int strokeSize) {
			this.x = x;
			this.y = y;
			this.color = color;
			this.strokeSize = strokeSize;
		}

		public int getStrokeSize() {
			return strokeSize;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Color getColor() {
			return color;
		}
	}

	public class Shape {
		private int x, y, width, height, strokeSize;
		private Color color;

		public Shape(int x, int y, int width, int height, int strokeSize, Color color) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.strokeSize = strokeSize;
			this.color = color;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getStrokeSize() {
			return strokeSize;
		}

		public Color getColor() {
			return color;
		}
	}

	public class Block extends Shape {
		public Block(int x, int y, int width, int height, int strokeSize, Color color) {
			super(x, y, width, height, strokeSize, color);
		}

		public Rectangle getRect() {
			return new Rectangle(getX(), getY(), getWidth(), getHeight());
		}
	}

}