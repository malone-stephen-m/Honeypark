import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Controller of the Ants class.
 * 
 * Original author: Kevin Workman
 * Modified by: Ari Kapusta and Adam Cantor
 */
public class AntsControlPanel {

	private Ants ants;
	//private Bfs bfs;
	private JPanel panel = new JPanel();
	private JButton timerButton = new JButton("\u25BA");



	private final AdvancedControlPanel advancedPanel;
	private int rows = 25;
	private int columns = 25;
	


	private Timer stepTimer = new Timer(0, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			step();
		}
	});

	public AntsControlPanel(final Ants ants, final AdvancedControlPanel advancedPanel){
		this.ants = ants;
		this.advancedPanel = advancedPanel;

		Dimension controlDimension = new Dimension(100, 25);

		timerButton.setMinimumSize(controlDimension);
		timerButton.setMaximumSize(controlDimension);
		timerButton.setPreferredSize(controlDimension);
		timerButton.setFocusable(false);
		timerButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		timerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(stepTimer.isRunning()){
					pause();
				}
				else{
					start();
				}
			}
		});

		final JButton stepButton = new JButton("Step");
		stepButton.setMinimumSize(controlDimension);
		stepButton.setMaximumSize(controlDimension);
		stepButton.setPreferredSize(controlDimension);
		stepButton.setFocusable(false);
		stepButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		stepButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				pause();
				step();
			}
		});
		
		final JButton scen1Button = new JButton("Scen 1");
		scen1Button.setMinimumSize(controlDimension);
		scen1Button.setMaximumSize(controlDimension);
		scen1Button.setPreferredSize(controlDimension);
		scen1Button.setFocusable(false);
		scen1Button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		scen1Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				start_scen1();
			}
		});
		
		final JButton scen2Button = new JButton("Scen 2");
		scen2Button.setMinimumSize(controlDimension);
		scen2Button.setMaximumSize(controlDimension);
		scen2Button.setPreferredSize(controlDimension);
		scen2Button.setFocusable(false);
		scen2Button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		scen2Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				start_scen2();
			}
		});
		
		final JButton scen3Button = new JButton("Scen 3");
		scen3Button.setMinimumSize(controlDimension);
		scen3Button.setMaximumSize(controlDimension);
		scen3Button.setPreferredSize(controlDimension);
		scen3Button.setFocusable(false);
		scen3Button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		scen3Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				start_scen3();
			}
		});
		
		final JButton scen4Button = new JButton("Scen 4");
		scen4Button.setMinimumSize(controlDimension);
		scen4Button.setMaximumSize(controlDimension);
		scen4Button.setPreferredSize(controlDimension);
		scen4Button.setFocusable(false);
		scen4Button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		scen4Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				start_scen4();
			}
		});
		
		/*
		 * Stuff for BFS implementation
		final JButton bfsButton = new JButton("BFS");
		bfsButton.setMinimumSize(controlDimension);
		bfsButton.setMaximumSize(controlDimension);
		bfsButton.setPreferredSize(controlDimension);
		bfsButton.setFocusable(false);
		bfsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		bfsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				start_bfs();
			}
		});
		*/

		


		
		final JCheckBox showAdvanced = new JCheckBox("Advanced");
		showAdvanced.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		showAdvanced.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				advancedPanel.getPanel().setVisible(showAdvanced.isSelected());
			}
			
		});
		advancedPanel.getPanel().setVisible(showAdvanced.isSelected());
		
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(timerButton);
		panel.add(stepButton);
		panel.add(scen1Button);
		panel.add(scen2Button);
		panel.add(scen3Button);
		panel.add(scen4Button);
	}

	public void start(){
		stepTimer.restart();
		timerButton.setText("Pause");
	}

	public void pause(){
		stepTimer.stop();
		timerButton.setText("\u25BA");
	}
	
	public void start_scen1(){
		ants.setMaxAnts(100);
		AntsControlPanel.this.ants.setGridSize(50, 50);
		rows = 50;columns=50;
		ants.set_scenario1();
		ants.repaint();
		stepTimer.restart();
		
	}
	
	public void start_scen2(){
		ants.setMaxAnts(100);
		AntsControlPanel.this.ants.setGridSize(50, 50);
		rows = 50;columns=50;
		ants.set_scenario2();
		ants.repaint();
		stepTimer.restart();
		
	}
	
	public void start_scen3(){
		ants.setMaxAnts(100);
		AntsControlPanel.this.ants.setGridSize(50, 50);
		rows = 50;columns=50;
		ants.set_scen_fail1();
		ants.repaint();
		stepTimer.restart();
		
	}
	
	public void start_scen4(){
		ants.setMaxAnts(100);
		AntsControlPanel.this.ants.setGridSize(50, 50);
		rows = 50;columns=50;
		ants.set_scen_spiral();
		ants.repaint();
		stepTimer.restart();
		
	}
	
	/*
	 * Stuff for BFS implementation
	public void start_bfs(){
		
			
		for(int column = 0; column < columns; column++){
			for(int row = 0; row < rows; row++){
				//if(cellArray[column][row].isFrontier()){
					//ants.addFrontierList(new State(new NodePlace(row,column),null,0));
				//}
			}
			//private Node newNode = new Node(Triplet([clickedCellColumn],[clickedCellRow],0),null,0);
			//bfs_frontier_list.add(newNode);
			ants.setMaxAnts(0);
			ants.repaint();
			stepTimer.restart();
		}
	}
	*/

	public void step(){
		ants.step();
		advancedPanel.step();
	}

	public JPanel getPanel(){
		return panel;
	}
}
