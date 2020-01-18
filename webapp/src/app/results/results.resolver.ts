import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class ResultsResolver implements Resolve<Object> {
  constructor(private httpClient: HttpClient) {}

  async resolve(route: ActivatedRouteSnapshot) {
    const keyword: string = route.queryParams.q;
    return await this.httpClient.get('/FbSma/search', { params: { keyword } }).toPromise();
  }
}
