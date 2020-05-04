const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendChatNotification = functions.database.ref('/Notification/Messages/{pushId}')
    .onWrite(event => {
        const message = event.data.current.val();
        const senderUid = message.ownerUid;
        const receiverUid = message.receiverUid;
		const ownerName = message.ownerName;
		const lastmessage = message.lastmessage;
        const promises = [];

        if (senderUid == receiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/UserActivities/${receiverUid}/ChatTokens`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(senderUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0];
            const owner = results[1];
			
			// Check if there are any device tokens.
			if (!instanceId.hasChildren()) {
				return console.log('There are no notification tokens to send to.');
			}
	
            console.log('notifying ' + receiverUid + ' about ' + lastmessage + ' from ' + senderUid + ownerName);

            const payload = {
                notification: {
                    title: ownerName,
					tag:"CHAT",
                    body: lastmessage,
                    sound: "default"
                },
				data :{
					blogpost: message.key,
					receiverid: message.ownerUid
				}
            };
			
			// Listing all tokens.
			const tokens = Object.keys(instanceId.val());

            admin.messaging().sendToDevice(tokens, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
	
exports.sendApplyNotification = functions.database.ref('/ApplyNotification/Applications/{pushId}')
    .onWrite(event => {
        const applymessage = event.data.current.val();
        const applysenderUid = applymessage.ownerUid;
		const applyownerName = applymessage.ownerName;
        const applyreceiverUid = applymessage.receiverUid;
        const promises = [];

        if (applysenderUid == applyreceiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/UserActivities/${applyreceiverUid}/ApplyTokens`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(applysenderUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0];
            const owner = results[1];
			
			// Check if there are any device tokens.
			if (!instanceId.hasChildren()) {
				return console.log('There are no notification tokens to send to.');
			}
	
            console.log('notifying ' + applyreceiverUid + ' about new application ' + applyownerName + applysenderUid);

            const payload = {
                notification: {
                    title: "24Hires",
					tag:"APPLICATION",
                    body: "You receive a new application from "+ applyownerName,
                    sound: "default"
                },
				data :{
					receiverid: applymessage.receiverUid
				}
            };
			
			// Listing all tokens.
			const tokens = Object.keys(instanceId.val());

            admin.messaging().sendToDevice(tokens, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
	
exports.sendShortlistedNotification = functions.database.ref('/ShortlistedNotification/ShortListed/{pushId}')
    .onWrite(event => {
        const shortlistmessage = event.data.current.val();
        const shortlistsenderUid = shortlistmessage.ownerUid;
		const shortlistownerName = shortlistmessage.ownerName;
        const shortlistreceiverUid = shortlistmessage.receiverUid;
        const promises = [];

        if (shortlistsenderUid == shortlistreceiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/UserActivities/${shortlistreceiverUid}/ShortlistTokens`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(shortlistsenderUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0];
            const owner = results[1];
			
			// Check if there are any device tokens.
			if (!instanceId.hasChildren()) {
				return console.log('There are no notification tokens to send to.');
			}
	
            console.log('notifying ' + shortlistreceiverUid + ' shortlisted by ' + shortlistownerName + shortlistsenderUid);

            const payload = {
                notification: {
                    title: "24Hires",
					tag:"SHORTLISTED",
                    body: "Congratulations! You are being shortlisted by "+ shortlistownerName,
                    sound: "default"
                },
				data :{
					receiverid: shortlistmessage.receiverUid
				}
            };
			
			// Listing all tokens.
			const tokens = Object.keys(instanceId.val());

            admin.messaging().sendToDevice(tokens, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
	
exports.sendHiredNotification = functions.database.ref('/HireNotification/Hire/{pushId}')
    .onWrite(event => {
        const hiremessage = event.data.current.val();
        const hiresenderUid = hiremessage.ownerUid;
		const hireownerName = hiremessage.ownerName;
        const hirereceiverUid = hiremessage.receiverUid;
        const promises = [];

        if (hiresenderUid == hirereceiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/UserActivities/${hirereceiverUid}/HireTokens`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(hiresenderUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0];
            const owner = results[1];
			
			// Check if there are any device tokens.
			if (!instanceId.hasChildren()) {
				return console.log('There are no notification tokens to send to.');
			}
	
            console.log('notifying ' + hirereceiverUid + ' hired by ' + hireownerName + hiresenderUid);

            const payload = {
                notification: {
                    title: "24Hires",
					tag:"HIRED",
                    body: "Congratulations! You are being hired by "+ hireownerName,
                    sound: "default"
                },
				data :{
					receiverid: hiremessage.receiverUid
				}
            };
			
			// Listing all tokens.
			const tokens = Object.keys(instanceId.val());

            admin.messaging().sendToDevice(tokens, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
	
exports.sendBookingNotification = functions.database.ref('/BookingNotification/Booking/{pushId}')
    .onWrite(event => {
        const bookingmessage = event.data.current.val();
        const bookingsenderUid = bookingmessage.ownerUid;
		const bookingownerName = bookingmessage.ownerName;
        const bookingreceiverUid = bookingmessage.receiverUid;
        const promises = [];

        if (bookingsenderUid == bookingreceiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/UserActivities/${bookingreceiverUid}/BookingTokens`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(bookingsenderUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0];
            const owner = results[1];
			
			// Check if there are any device tokens.
			if (!instanceId.hasChildren()) {
				return console.log('There are no notification tokens to send to.');
			}
	
            console.log('notifying ' + bookingreceiverUid + ' booked by ' + bookingownerName + bookingsenderUid);

            const payload = {
                notification: {
                    title: "24Hires",
					tag:"BOOKING",
                    body: "You have 1 new booking from "+ bookingownerName,
                    sound: "default"
                },
				data :{
					receiverid: bookingmessage.receiverUid
				}
            };
			
			// Listing all tokens.
			const tokens = Object.keys(instanceId.val());

            admin.messaging().sendToDevice(tokens, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
	
/*exports.sendJobNotification = functions.database.ref('/Job/{location}/{pushID}')
    .onWrite(event => {
		const joblocation =	event.params.location;
       const jobID = event.params.pushID;
		const jobdetails = event.data.current.val();
		const jobtitle = jobdetails.title;
		const jobcity = jobdetails.city;
        const promises = [];

        const getInstanceIdPromise = admin.database().ref('/UserLocation').orderByChild('CurrentCity').equalTo(joblocation).once('value');
     
        return Promise.all([getInstanceIdPromise]).then(results => {
            const instanceId = results[0];
			const receiverUidList = instanceId.val();
			var receiverItems;
			var receiverUid;
			
            console.log(instanceId);
			console.log(receiverUidList);
			
			instanceId.forEach(function(child) {
				receiverUid = child.key;
				console.log(child.key);
				receiverItems = child.val();
				console.log(receiverItems);
				
				admin.database().ref("/UserActivities/"+receiverUid+"/JobTokens").once('value').then(response => {
				
				const tokensToSend = [];
				// Listing all tokens.
				const tokens = Object.keys(response.val());
				console.log(tokens);
				
				const payload = {
					notification: {
						title: "New Job Nearby!",
						tag:"JOB",
						body: jobtitle,
						sound: "default"
					},
					data :{
					//	jobpost: jobID,
						jobcity: jobcity
					}
				};

				admin.messaging().sendToDevice(tokens, payload)
					.then(function (response) {
						console.log("Successfully sent message:", response);
					})
					.catch(function (error) {
						console.log("Error sending message:", error);
					});
					
				
			});
				
			});
        });
    });
*/