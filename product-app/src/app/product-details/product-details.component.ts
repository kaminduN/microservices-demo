import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { environment } from '../../environments/environment';
import { Product } from './../product';
// import { Input } from '@angular/core/src/metadata/directives';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  @Input() product: Product;
  public loading = false;
  public deleted = false;
  public isError = false;
  constructor(private route: ActivatedRoute, 
              private http: HttpClient,
              private location: Location) { }
  
  ngOnInit() {
    this.getProduct();
  }

  getProduct(): void {
    //https://stackoverflow.com/a/45339049
    const id = this.route.snapshot.paramMap.get('id');
    console.log(id);
    
    // const url = env${id;
    // return this.http.get<Hero>(url).pipe(
    //   tap(_ => this.log(`fetched hero id=${id}`)),
    //   catchError(this.handleError<Hero>(`getHero id=${id}`))
    // );
    this.fetchProduct(id);
    // this.getProduct(id)
    //   .subscribe(product => this.product = product);
  }

  fetchProduct(id: string){
      console.log(environment.apiUrl +  environment.productApi + "/" + id);
      // this.isError = false;
      this.http.get<Product>(environment.apiUrl +  environment.productApi  + "/" + id)
        .subscribe(res => {
              console.log(res);
              this.product = res;
              // this.product.name = res.
              
            },
            err => {
              console.log('Error occured');
              this.isError = true;
            }
      );
  
    };
  
    goBack(): void {
      this.location.back();
    }

    deleteProduct(id: string): void{
      console.log('deleting.. '+ id);
        if(confirm("Are you sure to delete this product ?")) {
          console.log("calling delete functionality");
          this.loading = true;
          // actual delete operation
          // first image
          this.http.delete(environment.contentApiUrl + '/' + this.product.productimage)
                .subscribe(res => {
                  console.log(res);
                  // this.product.name = res.
                  // this.goBack(); // go back if success..
                  // alert("Product image deleted");
                  // this.goBack();
                  // once succeeded the description
                  this.deleteProductDesc(id);
                  this.isError = false;
                },
                err => {
                  console.log('Error occured while deleting image..');
                  console.log(err);
                  this.loading = false;
                  this.isError = true;
                  console.log("Product deletion faliure: code 1");
                }
          );
        }      
    }

  deleteProductDesc(id:string){
    this.http.delete(environment.apiUrl +  environment.productApi  + "/" + id)
    .subscribe(res => {
          console.log(res);
          // this.product.name = res.
          // this.goBack(); // go back if success..
          console.log("Product deleted");
          // this.deleteProductImage(this.product.productimage);
          // move this to next call since that is async
          // this.product = null;
          // this.loading = false;
          // this.deleted = true;
          // this.goBack();
          this.postDelete();
          
        },
        err => {
          console.log('Error occured while deleting..');
          console.log(err);
          console.log("Product deletion faliure: code 2");
          this.loading = false;
          this.isError = true;
        }
  );
  }

  postDelete(){
    this.product = null;
    this.loading = false;
    this.deleted = true;
    this.isError = false;
    setTimeout(()=>{
        this.goBack();  
    }, 3000);// wait 3secs befor going back to main page
    
  }

  getImageUrl(imageUrl: string) {
    return environment.contentApiUrl + '/' + imageUrl;
  }

  // used in the loading spinner
  imgloading: boolean = true
  onImageLoad() {
      this.imgloading = false;
  }

  setDefaultPic(event) {
    console.debug(event);
    event.target.src = environment.placeholdImage;
  }
}
