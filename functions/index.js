const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Jab bhi naya message save hoga, notification send hoga
exports.sendNotificationOnMessage = functions.database
  .ref("/messages/{conversationId}/{messageId}")
  .onCreate(async (snapshot, context) => {
    const messageData = snapshot.val();
    const receiverId = messageData.receiverId;
    const senderName = messageData.senderName || "New Message";

    // ✅ Receiver ka FCM token nikalna
    const userSnapshot = await admin.database()
      .ref(`/users/${receiverId}/fcmToken`).once("value");
    const fcmToken = userSnapshot.val();

    if (fcmToken) {
      const payload = {
        notification: {
          title: `New message from ${senderName}`,
          body: messageData.text || "You have a new message",
        },
      };

      try {
        await admin.messaging().sendToDevice(fcmToken, payload);
        console.log("✅ Notification sent to:", receiverId);
      } catch (error) {
        console.error("❌ Error sending notification:", error);
      }
    }
  });
