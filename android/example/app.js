var gcm = require('se.hyperlab.tigcm');

// called when we received a device token
function onRegister(e) {
  Ti.API.info('Got device token: ' + e.token);
  // some code that stores the token on the server side
}

// called when we receive a push notification
function onMessage(e) {
  Ti.API.info('Received push notification with data: ' + JSON.stringify(e.data));
}

// called when we failed to receive device token
function onError(e) {
  Ti.API.error(e.error);
}

gcm.registerForPushNotifications({
  onRegister: onRegister,
  onMessage: onMessage,
  onError: onError,
});
