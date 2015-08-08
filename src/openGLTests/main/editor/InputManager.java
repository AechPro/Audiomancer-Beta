package openGLTests.main.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class InputManager extends JFrame
{
	private static final long serialVersionUID = -3779592574769364483L;
	private int input = -1;
	private JTextField jtfText1;
	private TextHandler handler = null;
	private boolean informationAcquired=false;
	public InputManager(String msg)
	{
		super(msg);
		System.out.println("manager created");
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		jtfText1 = new JTextField(10);
		container.add(jtfText1);
		handler = new TextHandler();
		jtfText1.addActionListener(handler);
		setSize(325, 100);
		setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		while(!informationAcquired){try{Thread.sleep(16);} catch(Exception e){}}
	}
	private class TextHandler implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try{input = Integer.parseInt(e.getActionCommand()); informationAcquired=true;}
			catch(Exception ex){System.out.println("failed to parse information"); informationAcquired=false;}
		}
	}
	public int getInput(){return input;}
	public boolean isInformationAcquired(){return informationAcquired;}
}