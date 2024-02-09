$(document).ready(function () {

    var modal = $('#modal');

    var image = document.getElementById('sample_image');

    var cropper;

    var csrfToken = $("meta[name='_csrf']").attr("content");

    $('#upload_image').change(function (e) {
        if (cropper) {
            cropper.destroy();
            cropper = null;
        }
        var files = e.target.files;

        var done = function (url) {
            image.src = url;

            modal.modal('show');
        };

        if (files && files.length > 0) {
            reader = new FileReader();

            reader.onload = function (e) {

                done(reader.result);

            };

            reader.readAsDataURL(files[0]);
        }

    });

    modal.on('shown.bs.modal', function () {


        cropper = new Cropper(image, {
            aspectRatio: 1,
            viewMode: 3,
            preview: '.preview'
        });
    }).on('hidden.bs.modal', function () {

    });
    $('#crop').click(function () {
        canvas = cropper.getCroppedCanvas({
            width: 400,
            height: 400
        });


        $('#uploaded_image').attr('src', canvas.toDataURL("image/png"));
        modal.modal('hide');
    })



});