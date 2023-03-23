package chess;

import java.awt.Color;
public class Point {
	private int x;//横坐标
	private int y;//纵坐标
	private Color color;//棋子颜色
	public static final int CHESS_D=30;//棋子直径
	public Point(int x,int y,Color color){
		this.x=x;
		this.y=y;
		this.color=color;
	}
	public int getx() {
		return x;
	}
	public int gety() {
		return y;
	}
	public Color getcolor() {
		return color;
	}
}
