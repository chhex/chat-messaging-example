package chat

import static javax.swing.JFrame.EXIT_ON_CLOSE

import java.awt.*

import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import groovy.swing.SwingBuilder

def ChatStompClient chatClient = new ChatStompClient()
chatClient.host = "localhost:8080"
def swingBuilder = new SwingBuilder()
swingBuilder.edt {
	// edt method makes sure UI is build on Event Dispatch Thread.
	lookAndFeel 'nimbus'  // Simple change in look and feel.
	frame(title: 'Chat', size: [450, 550],
	show: true, locationRelativeTo: null,
	defaultCloseOperation: EXIT_ON_CLOSE) {
		borderLayout(vgap: 5)

		panel(constraints: BorderLayout.NORTH,
		border: compoundBorder([
			emptyBorder(10),
			titledBorder('Server Connection')
		])) {

			connectButton = button text: 'Connect', actionPerformed: {
				def userSessionId = retrieveUserSessionId()
				sessionIdField.text = userSessionId
				chatClient.userSessionId = userSessionId
				chatClient.connect()
				chatClient.chatMessageHandler = new ChatStompMessageHandler() {
					@Override
					public void handleMessage(Object payload) {
						chatField.append(payload.toString())
						chatField.append("\n")
					}							
				}
				toogleFields(this,true)
			}

			disconnectButton = button text: 'Disconnect', actionPerformed: {
			}

		}

		panel(constraints: BorderLayout.CENTER,
		border: compoundBorder([
			emptyBorder(10),
			titledBorder('Shout')
		])) {
			tableLayout {
				tr {
					td {
						label 'User Session Id:'  // text property is default, so it is implicit.
					}
					td {
						sessionIdField = textField "", id: 'sessionId', columns: 2
						sessionIdField.editable = false
						sessionIdField.background = Color.LIGHT_GRAY

					}
				}
				tr {
					td {
						label 'Shout something:'
					}
					td {
						shoutField = textField id: 'shout', columns: 20, text: ""
					}
				}
				tr {
					td {
						label 'To (Empty for all):'
					}
					td {
						toField = textField id: 'to', columns: 2, ""
					}
				}
				tr {
					td{
						sendButton = button text: 'Send', actionPerformed: {
							chatClient.sendSout(shoutField.text, toField.text)
						}
							
					}
				}
			}

		}
		panel(constraints: BorderLayout.SOUTH,
		border: compoundBorder([
			emptyBorder(10),
			titledBorder('Chat')
		])) {
			tableLayout {
				tr {
					td {
						chatField = textArea "", id: 'chat', columns: 30 , rows: 10
						chatField.editable = false
						chatField.background = Color.LIGHT_GRAY


					}
				}
			}

		}
		toogleFields(this,false)
	}

}
def toogleFields(swing, toogle) {
	swing.disconnectButton.enabled = toogle
	swing.shoutField.enabled = toogle
	swing.toField.enabled = toogle
	swing.sendButton.enabled = toogle
	swing.connectButton.enabled = !toogle

}

def retrieveUserSessionId() {
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/getUserSessionId", String.class);
	def output = response.getBody()
	println output
	output
}


