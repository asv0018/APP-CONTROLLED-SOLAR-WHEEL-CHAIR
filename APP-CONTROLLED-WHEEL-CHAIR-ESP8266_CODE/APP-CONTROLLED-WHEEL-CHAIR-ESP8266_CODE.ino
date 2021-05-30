#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <ArduinoJson.h>

#define GPIO_SR04_TRIG 2 // D2
#define GPIO_SR04_ECHO 4 // D4

# define MOTOR_IN1_A 14 // D5
# define MOTOR_IN1_B 12 // D6
# define MOTOR_IN2_A 13 // D7
# define MOTOR_IN2_B 15 // D8

# define EMERGENCY_SIG_PIN 5 // D1

// CREDENTIALS
const char* ssid = "ASV DEN";
const char* password = "87554321";

WiFiServer server(1337);

void setup(){
  Serial.begin(115200);
  Serial.println("Setting AP (Access Point)...");
  // Connect to the server
  WiFi.begin(ssid, password);
  while(WiFi.status()!=WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");
  // Start TCP server
  server.begin();
  Serial.println("Server started");
  // Obtain the Local IP
  Serial.println(WiFi.localIP());
  
  pinMode(EMERGENCY_SIG_PIN, OUTPUT);
  
  pinMode(MOTOR_IN1_A, OUTPUT);
  pinMode(MOTOR_IN1_B, OUTPUT);
  pinMode(MOTOR_IN2_A, OUTPUT);
  pinMode(MOTOR_IN2_B, OUTPUT);

  pinMode(GPIO_SR04_TRIG, OUTPUT);
  pinMode(GPIO_SR04_ECHO, INPUT);
  
  }

StaticJsonDocument<200> doc;

void loop() {
  bool forward_movement = false;
  WiFiClient client = server.available();
  if(client){
    Serial.println("Client connected...");
    while(client.connected()){
      digitalWrite(GPIO_SR04_TRIG, LOW);
      delayMicroseconds(2);
      // Sets the TRIGGER pin on HIGH state for 10 micro seconds
      digitalWrite(GPIO_SR04_TRIG, HIGH);
      delayMicroseconds(10);
      digitalWrite(GPIO_SR04_TRIG, LOW);
      // Reads the ECHOPIN, returns the sound wave travel time in microseconds
      float duration = pulseIn(GPIO_SR04_ECHO, HIGH);
      // Calculating the distance
      int distance= duration*0.034/2;
      //Serial.println(distance);
      
      char datas[40]={0};int i = 0;
        if(client.available()>0){
          Serial.println("@");
          while(client.available()){
            if(client.available()){
            char temp = client.read();
            if(temp=='\n'){
              break;
            }
            datas[i++] = temp;
            }
          }
          String json_data = String(datas);
          DeserializationError error = deserializeJson(doc, json_data);
          
          // Test if parsing succeeds.
          if (error) {
            Serial.print(F("deserializeJson() failed: "));
            Serial.println(error.f_str());
            return;
          }

          if(doc["f"]){
            Serial.println("MOVING FORWARD");
            if(distance <= 18){
              Serial.println("OBSTACLE FOUND, HENCE STOPPING.");
              STAY_IN_POSITION();
            }else{
              forward_movement = true;
              MOVE_FORWARD();
            }
            
          }else if(doc["b"]){
            Serial.println("MOVING BACKWARD");
            MOVE_BACKWARD();
          }else if(doc["l"]){
            Serial.println("MOVING LEFT");
            TURN_LEFT();
          }else if(doc["r"]){
            Serial.println("MOVING RIGHT");
            TURN_RIGHT();
          }else{
            Serial.println("STAYING IN POSITION");
            STAY_IN_POSITION();
            forward_movement = false;
          }
          
          if(doc["e"]){
            Serial.println("TRIGGERING EMERGENCY CALL");
            TRIGGER_EMERGENCY_SIGNAL();
          }
          
        }

        if(distance <= 18){
            if(forward_movement){
              Serial.print("OBSTACLE FOUND IN FRONT, HENCE STOPPED, UNTIL ITS CLEARENCE");
              STAY_IN_POSITION();  
            }
          }
    }
    Serial.println("Client disconnected :<, MOVEMENT STOPPED");
    STAY_IN_POSITION();
    forward_movement = false;
    client.stop();
  }
}

void MOVE_FORWARD(){
  digitalWrite(MOTOR_IN1_A, LOW);
  digitalWrite(MOTOR_IN1_B, HIGH);
  digitalWrite(MOTOR_IN2_A, LOW);
  digitalWrite(MOTOR_IN2_B, HIGH);
}

void MOVE_BACKWARD(){
  digitalWrite(MOTOR_IN1_A, HIGH);
  digitalWrite(MOTOR_IN1_B, LOW);
  digitalWrite(MOTOR_IN2_A, HIGH);
  digitalWrite(MOTOR_IN2_B, LOW);
}

void TURN_LEFT(){
  digitalWrite(MOTOR_IN1_A, LOW);
  digitalWrite(MOTOR_IN1_B, HIGH);
  digitalWrite(MOTOR_IN2_A, HIGH);
  digitalWrite(MOTOR_IN2_B, LOW);
}

void TURN_RIGHT(){
  digitalWrite(MOTOR_IN1_A, HIGH);
  digitalWrite(MOTOR_IN1_B, LOW);
  digitalWrite(MOTOR_IN2_A, LOW);
  digitalWrite(MOTOR_IN2_B, HIGH);
}

void STAY_IN_POSITION(){
  digitalWrite(MOTOR_IN1_A, LOW);
  digitalWrite(MOTOR_IN1_B, LOW);
  digitalWrite(MOTOR_IN2_A, LOW);
  digitalWrite(MOTOR_IN2_B, LOW);
}

void TRIGGER_EMERGENCY_SIGNAL(){
  digitalWrite(EMERGENCY_SIG_PIN, HIGH);
  delay(50);
  digitalWrite(EMERGENCY_SIG_PIN, LOW);
}
