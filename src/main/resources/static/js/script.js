console.log("This is scripting file");

const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");

	}
	else {

		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
}

tinymce.init({
	selector: '#mytextarea'
});

const deleteContact = (cid) => {
	Swal.fire({
		title: 'Are you sure?',
		text: "You want to delete this Contact",
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'Yes, delete it!'
	}).then((result) => {
		if (result.isConfirmed) {
			window.location.href = "/SmartContactManager/user/delete/" + cid;
		}
	})
}

const search=()=>{
	//console.log("searching...");
	let query=$('#search-input').val();
	
	if(query==""){
		$('.search-result').hide();
		
	}else{
		//console.log(query);
		
		
		//Sending Request
		let url=`http://localhost:8858/SmartContactManager/user/search/${query}`;
		fetch(url).then((response)=>{
			return response.json();
		}).then((data)=>{
			//data request
			//console.log(data);
			let text=`<div class='list-group'>`;
			
			data.forEach((contact)=>{
				//text+=`<img class="my-profile-picture" src="" th:src="@{'/img/'+${contact.image}}" alt="profile picture" />`;
				text+=`<a href='/SmartContactManager/user/${contact.contact_Id}/contact-details' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
			})
			
			text+=`</div>`;
			
			$('.search-result').html(text);
			$('.search-result').show();
		});
	}
}

$(document).ready(function() {
    $("#show_hide_password a").on('click', function(event) {
        event.preventDefault();
        if($('#show_hide_password input').attr("type") == "text"){
            $('#show_hide_password input').attr('type', 'password');
            $('#show_hide_password i').addClass( "fa-eye-slash" );
            $('#show_hide_password i').removeClass( "fa-eye" );
        }else if($('#show_hide_password input').attr("type") == "password"){
            $('#show_hide_password input').attr('type', 'text');
            $('#show_hide_password i').removeClass( "fa-eye-slash" );
            $('#show_hide_password i').addClass( "fa-eye" );
        }
    });
});


const readURL = (input) => {
	if (input.files && input.files[0]) {

		var reader = new FileReader();

		reader.onload = function(e) {
			$('.image-upload-wrap').hide();

			$('.file-upload-image').attr('src', e.target.result);
			$('.file-upload-content').show();

			$('.image-title').html(input.files[0].name);
		};

		reader.readAsDataURL(input.files[0]);

	} else {
		removeUpload();
	}
}

const removeUpload = () => {
	$('.file-upload-input').replaceWith($('.file-upload-input').clone());
	$('.file-upload-content').hide();
	$('.image-upload-wrap').show();
}
$('.image-upload-wrap').bind('dragover', function() {
	$('.image-upload-wrap').addClass('image-dropping');
});
$('.image-upload-wrap').bind('dragleave', function() {
	$('.image-upload-wrap').removeClass('image-dropping');
});


