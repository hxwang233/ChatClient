package chess;

import java.awt.Color;
public class Point {
	private int x;//������
	private int y;//������
	private Color color;//������ɫ
	public static final int CHESS_D=30;//����ֱ��
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
