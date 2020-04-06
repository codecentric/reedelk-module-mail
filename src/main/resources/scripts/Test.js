function Test(context,message) {
    var part = HttpPartBuilder
        .create('myfile')
        .attribute('filename','hello.txt')
        .text('Superz')
        .build();

    var attachment1 = MailAttachmentBuilder.create()
        .text('Myattachment')
        .filename('mytext.txt')
        .build();
    return {'attachment1': attachment1 }
}