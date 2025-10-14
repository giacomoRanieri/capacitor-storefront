import { WebPlugin } from '@capacitor/core';

import type { CapacitorStorefrontPlugin, Storefront } from './definitions';

export class CapacitorStorefrontWeb extends WebPlugin implements CapacitorStorefrontPlugin {
  getStorefront(): Promise<Storefront> {
    return Promise.resolve({countryCode: "IT"})
  }

  initialize(): Promise<void> {
    return Promise.resolve();
  }
}
