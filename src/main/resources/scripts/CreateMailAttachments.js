function CreateMailAttachments(context,message) {
	var att1 = MailAttachmentBuilder.create().filename('script').text('hello').build();
	return {'att1': att1 }
}