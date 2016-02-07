import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Random;

class Box
{
	private int properties;

	public Box()
	{
		properties = 0;
	}

	public int getProperties()
	{
		return properties;
	}

	public void setProperties(int properties)
	{
		if (!((properties >= 0 && properties <= 15) || properties == 31))
			throw new IllegalArgumentException("propety value not valid!");
		else
			this.properties = properties;
	}

	public char getTopBit()
	{
		String s = Integer.toBinaryString(properties);
		int l = s.length();
		return (s.charAt(l - 1));
	}

	public char getBottomBit()
	{
		String s = Integer.toBinaryString(properties);
		int l = s.length() - 3;
		if (l < 0)
			return '0';
		else
			return s.charAt(l);
	}

	public char getLeftBit()
	{
		String s = Integer.toBinaryString(properties);
		int l = s.length() - 4;
		if (l < 0)
			return '0';
		else
			return s.charAt(l);
	}

	public char getRightBit()
	{
		String s = Integer.toBinaryString(properties);
		int l = s.length() - 2;
		if (l < 0)
			return '0';
		else
			return s.charAt(l);
	}

	public char allSidesSet()
	{
		if (getLeftBit() == '1' && getRightBit() == '1'
				&& getBottomBit() == '1' && getTopBit() == '1')
			return '1';
		else
			return '0';
	}

	public char getPlayerBit()
	{
		String s = Integer.toBinaryString(properties);
		int l = s.length() - 5;
		if (l < 0)
			return '0';
		else
			return s.charAt(l);
	}

	public int getSidesSet()
	{
		int c = getTopBit() + getBottomBit() + getLeftBit() + getRightBit() - 4
				* '0';
		return c;
	}

	public void SetPlayerOwned()
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement e = stacktrace[3];
		String methodName = e.getMethodName();
//		System.out.println(methodName);

		if (allSidesSet() == '1' && methodName != "BotThink")
		{
			setProperties(properties + 16);
		}
	}
}

public class DotsnBoxes extends Applet implements MouseListener
{
	public static final int GRID = 6;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 4;
	public static final int LEFT = 8;
	public static final int PLAYER_OWNED = 16;

	Box[][] b = new Box[GRID][GRID];

	@Override
	public void init()
	{
		for (int i = 0; i < GRID; i++)
			for (int j = 0; j < GRID; j++)
				b[i][j] = new Box();

		addMouseListener(this);
	}

	@Override
	public void paint(Graphics g)
	{
		this.setSize(80 * GRID, 80 * GRID);
		int player_points = 0;
		int bot_points = 0;
		for (int i = 0; i <= GRID; i++)
		{
			for (int j = 0; j <= GRID; j++)
			{

				g.fillRect(i * 50 + 5, j * 50 + 5, 5, 5);

				if (i != GRID && j != GRID)
				{
//					System.out.println(i + ", " + j + " : "
//							+ Integer.toBinaryString(b[i][j].getProperties())
//							+ " Sides : " + b[i][j].getSidesSet());

					if (b[i][j].getTopBit() == '1')
						g.drawLine(i * 50 + 5, j * 50 + 5, i * 50 + 55,
								j * 50 + 5);

					if (j == GRID - 1 && b[i][j].getBottomBit() == '1')
						g.drawLine(i * 50 + 5, j * 50 + 55, i * 50 + 55,
								j * 50 + 55);

					if (b[i][j].getLeftBit() == '1')
						g.drawLine(i * 50 + 5, j * 50 + 5, i * 50 + 5,
								j * 50 + 55);

					if (i == GRID - 1 && b[i][j].getRightBit() == '1')
						g.drawLine(i * 50 + 55, j * 50 + 5, i * 50 + 55,
								j * 50 + 55);

					if (b[i][j].getPlayerBit() == '1')
					{
						player_points++;
						g.drawString("P", i * 50 + 25, j * 50 + 25);
					}

					if (b[i][j].getPlayerBit() == '0'
							&& b[i][j].allSidesSet() == '1')
					{
						bot_points++;
						g.drawString("B", i * 50 + 25, j * 50 + 25);
					}
				}
			}
		}

		g.drawString("Player : " + player_points,10 ,70*GRID );
		g.drawString("Bot : " + bot_points,10 ,70*GRID -20 );
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
		int x = me.getX();
		int y = me.getY();
//		System.out.println("Pos1 : " + x + ", " + y);
		if (y > 40)
			y = y + 10;
		if (x > 40)
			x = x + 10;
		int xbox = (x - 5) / 50;
		int ybox = (y - 5) / 50;
		x = (x - 5) % 50;
		y = (y - 5) % 50;
		int ret = 0;

		ret = gameplay(x, y, xbox, ybox);
		repaint();

		if (ret == 1)
		{
			BotPlay();
			repaint();
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	public int gameplay(int x, int y, int xbox, int ybox)
	{
//		System.out.println("Pos2 : " + x + ", " + y);
//		System.out.println("Box : " + xbox + ", " + ybox);
		if (xbox <= GRID && ybox <= GRID)
		{
			if (y >= 0 && y <= 20)
			// horizontal line
			{
				if (ybox == 0 && b[xbox][ybox].getTopBit() != '1')
				{
					b[xbox][ybox].setProperties(b[xbox][ybox].getProperties()
							+ TOP);

					b[xbox][ybox].SetPlayerOwned();
					return 1;
				}

				else if (ybox == GRID
						&& b[xbox][ybox - 1].getBottomBit() != '1')
				{
					b[xbox][ybox - 1].setProperties(b[xbox][ybox - 1]
							.getProperties() + BOTTOM);

					b[xbox][ybox - 1].SetPlayerOwned();
					return 1;
				}
				else if (ybox > 0 && ybox < GRID)
				{
					if (b[xbox][ybox].getTopBit() != '1'
							&& b[xbox][ybox - 1].getBottomBit() != '1')
					{
						b[xbox][ybox].setProperties(b[xbox][ybox]
								.getProperties() + TOP);
						b[xbox][ybox - 1].setProperties(b[xbox][ybox - 1]
								.getProperties() + BOTTOM);

						b[xbox][ybox - 1].SetPlayerOwned();

						b[xbox][ybox].SetPlayerOwned();
						return 1;
					}
					else
						return 0;
				}
				else
					return 0;

			}
			else if (x >= 0 && x <= 20)
			// vertical line
			{

				if (xbox == 0 && b[xbox][ybox].getLeftBit() != '1')
				{
					b[xbox][ybox].setProperties(b[xbox][ybox].getProperties()
							+ LEFT);

					b[xbox][ybox].SetPlayerOwned();
					return 1;
				}

				else if (xbox == GRID && b[xbox - 1][ybox].getRightBit() != '1')
				{
					b[xbox - 1][ybox].setProperties(b[xbox - 1][ybox]
							.getProperties() + RIGHT);

					b[xbox - 1][ybox].SetPlayerOwned();
					return 1;
				}

				else if (xbox > 0 && xbox < GRID)
				{
					if (b[xbox][ybox].getLeftBit() != '1'
							&& b[xbox - 1][ybox].getRightBit() != '1')
					{
						b[xbox][ybox].setProperties(b[xbox][ybox]
								.getProperties() + LEFT);
						b[xbox - 1][ybox].setProperties(b[xbox - 1][ybox]
								.getProperties() + RIGHT);

						b[xbox - 1][ybox].SetPlayerOwned();

						b[xbox][ybox].SetPlayerOwned();
						return 1;
					}
					else
						return 0;
				}
				else
					return 0;
			}
			else
				return 0;

		}
		else
			return 0;

	}

	public void BotPlay()
	{
		int ret = 0;
		int sides[] = { 3, 0, 1, 2 };
		for (int i = 0; i < 4; i++)
			if (ret == 1)
				return;
			else
				ret = BotThink(sides[i]);
	}

	public static int[] generateRandomNumber(int[][] availableCollection)
	{
		Random rand = new Random();
		return availableCollection[rand.nextInt(availableCollection.length)];
	}

	int BotThink(int sides)
	{
		for (int i = 0; i < GRID; i++)
		{
			for (int j = 0; j < GRID; j++)
			{
				if (b[i][j].allSidesSet() != '1')
				{
					int[] left = { b[i][j].getLeftBit(), 8, 33 };
					int[] right = { b[i][j].getRightBit(), 8, 33 };
					int[] top = { b[i][j].getTopBit(), 47, 3 };
					int[] bottom = { b[i][j].getBottomBit(), 47, 3 };

					if (b[i][j].getSidesSet() == sides)
					{
						int[][] collection = { top, bottom, left, right };
						int[] random;
						do
						{
							random = generateRandomNumber(collection);
//							System.out.println(random[0] - '0' + " : " + sides);
						}
						while (random[0] == '1');

						if (Arrays.equals(random, left)
								|| Arrays.equals(random, top))
							gameplay(random[1], random[2], i, j);
						else if (Arrays.equals(random, right))
							gameplay(random[1], random[2], i + 1, j);
						else if (Arrays.equals(random, bottom))
							gameplay(random[1], random[2], i, j + 1);

						return 1;
					}
				}
			}
		}
		return 0;
	}
}
