
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Calculate extends Application{
	static double[] hypotheses;
	static double[][] observations;

	@Override
	public void start(Stage arg0) throws NumberFormatException {
		arg0.setTitle("Naive Bayes Classifier");
		// TODO Auto-generated method stub
		AnchorPane anchor = new AnchorPane();
		arg0.show();
		try{
			TextInputDialog tid = new TextInputDialog("");
			tid.setContentText(null);
			tid.setGraphic(null);
			
			//Determines number of hypotheses to be tested and probabilities of each
			tid.setHeaderText("Number of hypotheses?");
			tid.setTitle("Hypotheses");
			Optional<String> result = tid.showAndWait();	
			if(result.isPresent()){
				hypotheses = new double[Integer.parseInt(result.get())];
				for(int i=0;i<hypotheses.length;i++){
					tid.setHeaderText("P(H" + (i+1) +"): ");
					tid.getEditor().setText("");
					Optional<String> res = tid.showAndWait();
					if(res.isPresent()) hypotheses[i] = Double.parseDouble(res.get());
					else System.exit(1);
					if(hypotheses[i] > 1 || hypotheses[i] < 0) throw new NumberFormatException("Probabilities must be bound by 0 and 1 inclusive");
				}
			}
			else System.exit(1);
			
			//Determines number of observations and probabilities of observation conditioned on each hypothesis
			tid.setHeaderText("Number of observations?");
			tid.setTitle("Observations");
			tid.getEditor().setText("");
			result = tid.showAndWait();
			if(result.isPresent()){
				observations = new double[Integer.parseInt(result.get())][hypotheses.length];
				for(int j=0;j<observations.length;j++){
					for(int k=0;k<hypotheses.length;k++){
						tid.setHeaderText("P(X=" + j + "|H" + (k+1) + "): ");
						tid.getEditor().setText("");
						Optional<String> res = tid.showAndWait();
						if(res.isPresent()) observations[j][k] = Double.parseDouble(res.get());
						else System.exit(1);
						if(observations[j][k] > 1 || observations[j][k] < 0) throw new NumberFormatException("Probabilities must be bound by 0 and 1 inclusive");
					}
				}
			}
			else System.exit(1);
			
			//UI stuff
			VBox list = calculate(new VBox(10));
			anchor.getChildren().add(list);
			Scene scene = new Scene(anchor,arg0.getWidth(),arg0.getHeight());
			arg0.setScene(scene);
		} catch(NumberFormatException e){
			Alert exception = new Alert(AlertType.ERROR);
			exception.setContentText(e.getMessage());
			exception.showAndWait();
			start(arg0);
		}
	}
	
	//Calculates and puts results of Naive Bayes into UI stuff
	public static VBox calculate(VBox list){
		double max = 0.0;
		double curr;
		String name;
		String map = "";
		double omega = 0.0;
		
		Label title = new Label("Naive Bayes Classification");
		title.setFont(Font.font("Lucida Sans",48));
		list.getChildren().add(title);
		for(int i=0;i<hypotheses.length;i++){
			curr = 1.0;
			name = "X=";
			for(int j=0;j<observations.length;j++){
				
				curr *=observations[j][i];
				name += j + " & " + "X=";
			}
			//P(H_i)
			curr *=hypotheses[i];
			omega +=curr;
			if(curr > max){
				max = curr;
				map = "P(H" + (i+1) + "|" + name.substring(0,name.length()-5) + ")";
			}
			Label ph = new Label("=>P(H" + (i+1) +"|" + name.substring(0,name.length()-5) + "): " + curr);
			ph.setFont(Font.font("Lucida Sans",16));
			list.getChildren().add(ph);
		}
		Label mapHyp = new Label("MAP Hypothesis is " + map +": " + max/omega);
		mapHyp.setFont(Font.font("Lucida Sans",24));
		list.getChildren().add(mapHyp);
		return list;
	}
}
