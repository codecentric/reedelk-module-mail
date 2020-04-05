function Test(context,message) {
    var part = HttpPartBuilder
        .create('myfile')
        .attribute('filename','hello.txt')
        .text('Superz')
        .build();
    return {'myfile': part}
}