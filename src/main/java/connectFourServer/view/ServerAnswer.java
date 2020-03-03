package connectFourServer.view;

/**
 * This class is a simple class returning a boolean answer to a question
 * asked by the client
 *
 * @author Richie Duggan
 */
public class ServerAnswer {
	
  private boolean answer;

  public ServerAnswer(boolean answer) {
	  this.answer = answer;
  }
  
  public boolean getAnswer() {
	  return answer;
  }
}
