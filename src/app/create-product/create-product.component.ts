import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { Product } from './../product'
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { HttpClient, HttpResponse,  HttpHeaders, 
         HttpEvent, HttpRequest, HttpEventType, 
         HttpErrorResponse} from '@angular/common/http';

// import { AbstractControl } from '@angular/forms/src/model';
import {Observable} from 'rxjs/Observable';
import { HttpParams } from '@angular/common/http/src/params';
// import { HttpErrorResponse } from '@angular/common/http/src/response';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';


@Component({
  selector: 'app-create-product',
  templateUrl: './create-product.component.html',
  styleUrls: ['./create-product.component.css']
})
export class CreateProductComponent implements OnInit {
  myForm: FormGroup;
  imageUrl: string;
  isSuccess: boolean = true;
  isSubmitted: boolean = false;
  currentFileUpload: File;

  progress: { percentage: number } = { percentage: 0 }
  errormessage = 'Fail.. try submitting again.';
  isValidUpload: boolean = true;

  constructor(private fb: FormBuilder, private http: HttpClient) { }

  ngOnInit() {
    this.myForm = this.fb.group({
      name: ['', [Validators.required, Validators.pattern('[a-zA-Z0-9]*'), Validators.nullValidator]],
      price: ['', [Validators.required, Validators.pattern('[0-9.]*')]],
      description: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      productImage: ['', Validators.required ],
      image: null
    });
    this.imageUrl = environment.defaultimageUrl;

    this.myForm.statusChanges.subscribe(x => {
      // https://stackoverflow.com/questions/43680043/control-reset-event-in-angular
      setTimeout(()=>{console.log(this.myForm.pristine);
        if (this.myForm.pristine) {
          // form reset action trigerd..
          this.imageUrl = environment.defaultimageUrl;
        }}, 0);
      
    });
  }

  onFileChange(fileInput: any){
    console.log('called....');
    
    var allowedExtensions = ["jpg","jpeg","png","JPG","JPEG","JFIF","BMP","SVG"];
    // this.imageUrl = fileInput.target.files[0];
    let fname = fileInput.target.files[0].name;
    var fileExtension = fname.split('.').pop().toLowerCase();  //file extension from input file
    

    let fsize = fileInput.target.files[0].size;
    var isSuccess = ((allowedExtensions.indexOf(fileExtension) > -1) && (fsize <= 5 * 1024 *1024));  //is extension in acceptable types

    // let isSuccess = allowedExtensions.indexOf(fileExtension) > -1;  //is extension in acceptable types
    // console.log(fileInput.target.files[0].size);



    // let fsize = fileInput.target.files[0].size;
    if(fsize> 1 * 1024 *1024) //do something if file size more than 1 mb (1048576)
    {
      console.log(fsize +" bites\n(File: "+fname+") Too big!");
    }else{
      console.log(fsize +" bites\n(File :"+fname+") You are good to go!");
    }
    
    if (! isSuccess) {
      // this.myForm.controls['productImage'].setErrors({'incorrect': true});
      this.resetFileInput();
      return;
    }

    // storing file content
    if(fileInput.target.files.length > 0) {
      let file = fileInput.target.files[0];
      this.myForm.controls['image'].setValue(file);
    }


    let reader = new FileReader();

    reader.onload = (e: any) => {
      if (isSuccess) {
        this.imageUrl = e.target.result;
      }else{
        // // reset the file input and raise validation error
        // this.myForm.controls['productImage'].setErrors({'incorrect': true});
        // this.myForm.controls['productImage'].reset();
        // this.imageUrl = environment.defaultimageUrl;
        this.resetFileInput();
      }
        
    }

    console.log('fileExtension', fileExtension, isSuccess);
   
    reader.readAsDataURL(fileInput.target.files[0]);
}

resetFileInput(){
  // reset the file input and raise validation error
  this.myForm.controls['productImage'].setErrors({'incorrect': true});
  // this.myForm.controls['productImage'].reset();
  this.imageUrl = environment.defaultimageUrl;
}

// filetypeValidator(control: AbstractControl): any {
  //   if (this.tags && this.tags.length > 0) {
  //       return null;
  //   }
  //   return {'tagsInvalid': true};
  // }

  onSubmit(form: FormGroup) {
    console.log(form.value);
    this.isSubmitted = true;
    // console.log('Valid?', form.valid); // true or false
    console.log('Name', form.value.name);
    // form.value.name = form.value.name.replace(/\s/g,'');
    // if( !form.value.name.trim().length ) {
    //   // only white-spaces
    //   console.log('invalid');
    //   this.isSuccess = false;
    //   return null;
    // }
    console.log('Name', form.value.name);
    console.log('Valid?', form.valid); // true or false
    console.log(form.value.name.replace(/\s/g,''));
    
    console.log('Price', form.value.price);
    console.log('Message', form.value.description);
    console.log('Message', form.value.productImage);
    console.log('size???', form.value.productImage.size);
    //TODO: actual submit of the details to the endopint
    // file and data submitting seperately
    if (form.valid) {
      console.log("Form Submitted!");
      // this.isSuccess = true;
    }else{
      this.isSuccess = false;
      return null;
    }
    
    interface UploadResp {
      productUrl: string;
      status: string;
      reason:string;
    }
    // let product: Product;
    let product = new Product(form.value.price, form.value.name, form.value.description)
    
    this.progress.percentage = 0;
    
    // file upload
    // this.addProductImage(form.value.productImage, form.value.name.replace(/\s/g,'')+ '.' +form.value.name.split('.').pop().toLowerCase())
    this.currentFileUpload = this.myForm.controls['image'].value;
    // this.addProductImage(this.currentFileUpload);
    let resp =this.addProductImage(this.currentFileUpload)
          .subscribe(event => {

      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round(100 * event.loaded / event.total);
      } else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
        console.log(event);
        console.log(event.body);
        let resp = event.body as UploadResp;
        product.productimage = resp.productUrl;
        this.addProduct(product);

        // this.myForm.reset();
      }
      // else{
      //   console.log('xxxxxxxxxxxxxx');
      //   console.log(event);
      // }
      // else if (event instanceof HttpErrorResponse) {
      //   console.log('File upload failed//!');
      // }
    },
    err => {
      console.log(err);
      
      console.log('Error occured in file upload..');
      this.errormessage += ': Error occured with file upload..' 
      this.isValidUpload = false;
      this.isSuccess = false;
      // return Observable.throw(err || 'Internal Server error');
      // return Observable.throw(err);
      // return; // no need to process further if this fails..!
    }
    );
    // // update product with that information
    console.log(resp);
    console.log(this.isSuccess);
    
    // if (this.isSuccess) {
    //   product.imageUrl = ''
    //   this.addProduct(product);  
    // }
    // this.currentFileUpload = undefined;
    // console.log('resp', resp);
    
  }


  addProduct(product: Product){
    const headers = new HttpHeaders()
    .set('Content-Type', 'application/json')
    let options = { headers: headers };
    console.log(JSON.stringify(product));
    
    this.http.post(environment.apiUrl + environment.productApi, JSON.stringify(product), options)
      .subscribe(res => {
        console.log(res);
        // this.products = res;
        console.log('addding product');
        
        this.isSuccess = true;
      }),
      err => {
        console.log('Error occured');
        this.isSuccess = false;
      }
  }

  addProductImage(image: File): Observable<HttpEvent<{}>> {
    // image = this.myForm.controls['image'].value
    let formdata: FormData = new FormData();
    formdata.append('file', image);
    console.log(formdata);
    console.log(image);

    const headers = new HttpHeaders()
    var timestamp = Number(new Date());
    // let fileUploadUrl = environment.apiUrl + '/contents/upload/' + timestamp +'-'+ name;
    let fileUploadUrl = environment.contentApiUrl + '/content/upload';
    // let fileUploadUrl = environment.contentApiUrl + '/files/somename.jpeg';
    const req = new HttpRequest('POST', fileUploadUrl, formdata, {
      headers: headers,
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);

  }

//   addProductImage(image: File) {
//     let formdata: FormData = new FormData();
//     formdata.append('file', image);
//     const headers = new HttpHeaders()
//     var timestamp = Number(new Date());
//     // let fileUploadUrl = environment.apiUrl + '/contents/upload/' + timestamp +'-'+ name;
//     let fileUploadUrl = environment.apiUrl + '/contents/upload/';
//     this.http.post(fileUploadUrl, formdata)
//           .catch( error => {
//             // here we can show an error message to the user,
//             // for example via a service
//             console.error("error catched", error);
//             return Observable.throw(error.message || error);
//             // return Observable.of({description: "Error Value Emitted"});
//       })
//       .subscribe(
//       (val) => {
//           console.log("POST call successful value returned in body", 
//                       val);
//       },
//       err => {
//           console.log("POST call in error", err);
//           this.isSuccess = false;
//       },
//       () => {
//           console.log("The POST observable is now completed.");
// });
//   }

}
