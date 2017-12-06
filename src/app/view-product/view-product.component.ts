import { Component, OnInit } from '@angular/core';
// import { Http, Response, Headers } from '@angular/http';
import { HttpClient, HttpResponse,  HttpHeaders } from '@angular/common/http';

import { environment } from '../../environments/environment';

@Component({
  selector: 'app-view-product',
  templateUrl: './view-product.component.html',
  styleUrls: ['./view-product.component.css']
})
export class ViewProductComponent implements OnInit {

  constructor(private http: HttpClient) { }

  id: number;
  // private headers = new Headers ({'Content-Type': 'application/json'});
  products = [];
  // contentApiUrl: string = environment.contentApiUrl;

  fetchData = function () {
    this.http.get(environment.apiUrl + '/products')
      .subscribe(res => {
            console.log(res);
            this.products = res.tripData;
          },
          err => {
            console.log('Error occured');
          }
    );
  };

  ngOnInit() {
    this.fetchData();
  }

  setDefaultPic(event) {
    console.debug(event);
    event.target.src = environment.placeholdImage;
  }

  // used in the loading spinner
  loading: boolean = true
  onImageLoad() {
      this.loading = false;
  }

  getImageUrl(imageUrl : string){
    return environment.contentApiUrl + '/' + imageUrl;
  }
}
