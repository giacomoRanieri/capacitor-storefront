import { WebPlugin } from '@capacitor/core';

import type { CapacitorStorefrontPlugin } from './definitions';

export class CapacitorStorefrontWeb extends WebPlugin implements CapacitorStorefrontPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
