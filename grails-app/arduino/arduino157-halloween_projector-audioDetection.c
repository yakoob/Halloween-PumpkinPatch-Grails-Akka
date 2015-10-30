#include <PubSubClient.h>

#include <Bridge.h>
#include <YunServer.h>
#include <YunClient.h>
#include <Servo.h>

YunServer server;
Servo projector;

YunClient net;
IPAddress mqttServerAddress(192, 168, 20, 114);

PubSubClient mqttClient(net);

int soundAnalogPin = 0;
int sound_value = 0;

long lastReconnectAttempt = 0;
long lastAudioSensorPublish = 0;

boolean reconnect() {
  if (mqttClient.connect("arduinoClient157")) {
    mqttClient.publish("arduionoClient_reconnected","OK");
  }
  return mqttClient.connected();
}

void setup() {

  projector.attach(6);

  Bridge.begin();

  server.listenOnLocalhost();
  server.begin();

  Serial.begin(9600);

  mqttClient.setServer(mqttServerAddress,1883);
  mqttClient.connect("arduinoClient_157");

}

void loop() {

  if (!mqttClient.connected()) {
    long now = millis();
    if (now - lastReconnectAttempt > 5000) {
      lastReconnectAttempt = now;
      // Attempt to reconnect
      if (reconnect()) {
        lastReconnectAttempt = 0;
      }
    }
  } else {
    mqttClient.loop();
  }

  YunClient client = server.accept();

  if (client) {
    process(client);
    client.stop();
  }

  long audioSensorNow = millis();
  if (audioSensorNow - lastAudioSensorPublish > 25) {
    lastAudioSensorPublish = audioSensorNow;
    sound_value = analogRead(soundAnalogPin);
    if (sound_value) {
      String str = "{'Value':'";
      str += sound_value;
      str = str + "'}";
      int str_len = str.length() + 1;
      char char_array[str_len];
      str.toCharArray(char_array, str_len);
      mqttClient.publish("Aurduino/HomeAutomation.Audio/102/event",char_array);
    }
  }

}

void process(YunClient client) {

  String command = client.readStringUntil('/');

  // Check if the url contains the word "servo"
  if (command == "servo") {
    servoCommand(client);
  }

}

void servoCommand(YunClient client) {

  int pin;
  int value;

  // Get the servo Pin
  pin = client.parseInt();

  // Check if the url string contains a value (/servo/6/VALUE)
  if (client.read() == '/') {

    value = client.parseInt();

    // projector
    if (pin == 6) {
      projector.write(value);
      if (value == 0) { // projector on
        mqttClient.publish("Aurduino/HomeAutomation.Servo/100/event","{'Value':'1'}");
      } else {
        mqttClient.publish("Aurduino/HomeAutomation.Servo/100/event","{'Value':'0'}");
      }

    }

  }

}

