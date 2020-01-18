import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'sma-results',
  templateUrl: './results.component.html',
  styles: [],
})
export class ResultsComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  mergeKeywords: string;
  results: any[];

  ngOnInit() {
    this.mergeKeywords = this.route.snapshot.queryParams.q || '';
    this.results = this.route.snapshot.data.results;

    console.log(this.results);
  }

  get keywords() {
    return this.mergeKeywords.split(';').join(', ');
  }
}
