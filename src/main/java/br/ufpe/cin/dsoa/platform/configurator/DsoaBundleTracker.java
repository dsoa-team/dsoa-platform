package br.ufpe.cin.dsoa.platform.configurator;

import java.util.*;
import org.osgi.framework.*;

public abstract class DsoaBundleTracker {

	final Set<Bundle> m_bundleSet = new HashSet<Bundle>();
	final BundleContext m_context;
	final SynchronousBundleListener m_listener;
	boolean m_open;

	public DsoaBundleTracker(BundleContext context) {
		m_context = context;
		m_listener = new SynchronousBundleListener() {
			public void bundleChanged(BundleEvent evt) {
				synchronized (this) {
					if (!m_open) {
						return;
					}
					if (evt.getType() == BundleEvent.STARTED) {
						if (!m_bundleSet.contains(evt.getBundle())) {
							m_bundleSet.add(evt.getBundle());
							addedBundle(evt.getBundle());
						}
					} else if (evt.getType() == BundleEvent.STOPPING) {
						if (m_bundleSet.contains(evt.getBundle())) {
							m_bundleSet.remove(evt.getBundle());
							removedBundle(evt.getBundle());
						}
					}
				}
			}
		};
	}

	public synchronized void open() {
		if (!m_open) {
			m_open = true;
			m_context.addBundleListener(m_listener);
			Bundle[] bundles = m_context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getState() == Bundle.ACTIVE) {
					m_bundleSet.add(bundles[i]);
					addedBundle(bundles[i]);
				}
			}
		}
	}

	public synchronized void close() {
		if (m_open) {
			m_open = false;
			m_context.removeBundleListener(m_listener);
			Bundle[] bundles = (Bundle[]) m_bundleSet
					.toArray(new Bundle[m_bundleSet.size()]);
			for (int i = 0; i < bundles.length; i++) {
				if (m_bundleSet.remove(bundles[i])) {
					removedBundle(bundles[i]);
				}
			}
		}
	}

	public synchronized Bundle[] getBundles() {
		return (Bundle[]) m_bundleSet.toArray(new Bundle[m_bundleSet.size()]);
	}

	protected abstract void addedBundle(Bundle bundle);

	protected abstract void removedBundle(Bundle bundle);
}
