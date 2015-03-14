The Imgur Extension allows you to upload screenshots directly out of Stencyl (using the camera button in Design Mode or Scene Designer).

To create your own version of this, you will need to go to http://api.imgur.com and obtain your own Client ID and Client Secret tokens for the OAuth process. To get these you will have to register a new application - you do have to have an Imgur account to do this (note, they allow login through other services such as Google so you probably already have this available). These are required by Imgur to be able to track how many uploads/requests your 'app' is performing daily, to prevent spam and abuse. Note that by having your own 'app', anybody else also using it is sharing that limit with you as tracked by Imgur by the Client ID.

Enjoy!

Version History

Initial Beta release; v0.5- 3/29/14
- (later same day) Bug-fix
- upload anonymously
- authorize access to user account process
- upload to user account
- other minor initial features for ease of use (copy/paste, etc)

Documentation:

Users can upload anonymously, or directly to their account. To upload to an account, the user has to take the following steps as detailed in the user documentation:

Authorization
Once the extension is installed and enabled for your game(s), you can immediately begin using it to upload to Imgur anonymously.

However, it is recommended that you link the extension to your Imgur user account. The steps to do so are as follows:
0. Be logged into Imgur, or be prepared to log into Imgur.
1. In Stencyl, select the menu option Extension > Extension Manager.
2. Select the Options for the Imgur Extension.
3. Within the Options Panel, press the top button labeled "Open Browser".
4. Your default web browser will launch and direct you to Imgur.
5. Imgur will ask you if you want to permit access to your account for this extension. Allow it.
6. Imgur will provide you with a PIN for the extension.
7. Enter this PIN into the Options Panel's "Imgur supplied auth PIN" field. (you can press the "paste from clipboard" button also)
8. Press "Submit PIN"
9. If your PIN was valid, Stencyl will now have the necessary authorizations to access your account.
10. Press the "Save" button on the Options panel to close the panel.

Options
Currently, the only other option besides authorization-related tasks is "Save Image To Disk". If you check this box, the extension will permit you to save the image to a file on your hard drive as normal. If it is unchecked, the Save File box will not appear.
This will permit you to both upload and save a local copy of your image if desired; or avoid the hassle of pressing "Cancel" every time that you don't want to save a local copy.

Uploading
	Whenever you press the Camera button to capture a screenshot in Stencyl, the Imgur extension is triggered. A small window will appear in the top left of your screen.

- Send to Imgur anonymously : this uploads the image anonymously (warning: at this time you will not have access to delete this image, or get the URL again later)
- Send to Imgur account : this uploads to your Imgur account. This button will not be enabled if you have not performed the authorization steps already.
- Copy URL to Clipboard : this copies the image URL to the system clipboard for easy use.