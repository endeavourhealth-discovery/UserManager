import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Pipe({
  name: 'linkify'
})
export class LinkifyPipe implements PipeTransform {

  constructor(private _domSanitizer: DomSanitizer) {}

  transform(value: any, type: string, title: string): any {
    return this._domSanitizer.bypassSecurityTrustHtml(this.stylize(value, type, title));
  }

  private stylize(dataArray: any, type : string, title: string): string {
    var stringArray = [];

    var url = window.location.protocol + "//" + window.location.host;

    var styledText = '<strong>' + title + '</strong>';
    for (let idx in dataArray) {
      styledText += '<a href="' + url + '/data-sharing-manager/#/' + type + '/' + idx + '/edit">' + dataArray[idx] + '</a>,';
    }

    return styledText.substring(0, styledText.length -1);
  }

}
