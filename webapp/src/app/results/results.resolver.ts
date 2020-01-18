import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class ResultsResolver implements Resolve<Object> {
  constructor(private httpClient: HttpClient) {}

  async resolve(route: ActivatedRouteSnapshot) {
    console.log('hola');

    const keyword: string = route.queryParams.q;
    const res = await this.httpClient.get('/FbSma/search', { params: { keyword } }).toPromise();
    console.log(res);
    return res;
  }
}
